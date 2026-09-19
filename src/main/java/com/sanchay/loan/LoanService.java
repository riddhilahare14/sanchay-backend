package com.sanchay.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanchay.exception.BadRequestException;
import com.sanchay.exception.ResourceNotFoundException;
import com.sanchay.member.Member;
import com.sanchay.member.MemberRepository;
import com.sanchay.state.AppState;
import com.sanchay.state.AppStateRepository;
import com.sanchay.state.AppStateService;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final AppStateService appStateService;
	private final MemberRepository memberRepository;
	private final AppStateRepository appStateRepository;

	public LoanService(
        LoanRepository loanRepository,
        AppStateService appStateService,
        MemberRepository memberRepository,
		AppStateRepository appStateRepository
	) {
		this.loanRepository = loanRepository;
		this.appStateService = appStateService;
		this.memberRepository = memberRepository;
		this.appStateRepository = appStateRepository;
	}

	public List<ActiveLoanResponse> getActiveLoans() {

		BigDecimal interestRate =
				appStateService.getAppState().getLoanInterestRate();
	
		return loanRepository.findByStatus(LoanStatus.ACTIVE)
				.stream()
				.map(loan -> {
	
					BigDecimal monthlyInterest;
	
					if (loan.isMonthlyPaid()) {
						// Payment already recorded this month.
						// Use the saved interest.
						monthlyInterest = loan.getMonthlyInterest();
					} else {
						// Payment not yet recorded.
						// Calculate the interest for this month.
						monthlyInterest = loan.getRemainingPrincipal()
								.multiply(interestRate)
								.setScale(0, RoundingMode.CEILING);
					}
	
					return new ActiveLoanResponse(
							loan.getId(),
							loan.getMember().getName(),
							loan.getRemainingPrincipal(),
							monthlyInterest,
							loan.getMonthlyPrincipalRepayment(),
							loan.getMonthlyTotalReceived(),
							loan.isMonthlyPaid()
					);
				})
				.toList();
	}

	public MonthlyLoanDetailResponse recordLoanPayment(
        LoanRepaymentRequest request
	) {
		Loan loan = loanRepository.findById(request.loanId())
				.orElseThrow(() ->
						new ResourceNotFoundException("Loan not found")
				);

		if (loan.getStatus() != LoanStatus.ACTIVE) {
			throw new BadRequestException("Loan is not active");
		}

		BigDecimal principalRepayment = request.principalRepayment();

		if (principalRepayment.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException(
					"Principal repayment must be greater than zero"
			);
		}

		if (principalRepayment.compareTo(
				loan.getRemainingPrincipal()) > 0) {
			throw new BadRequestException(
					"Principal repayment cannot be greater than remaining loan"
			);
		}

		BigDecimal interestRate =
				appStateService.getAppState().getLoanInterestRate();

		BigDecimal monthlyInterest = loan.getRemainingPrincipal()
				.multiply(interestRate)
				.setScale(0, RoundingMode.CEILING);

		BigDecimal totalReceived =
				principalRepayment.add(monthlyInterest);

		loan.setMonthlyPrincipalRepayment(principalRepayment);
		loan.setMonthlyInterest(monthlyInterest);
		loan.setMonthlyTotalReceived(totalReceived);
		loan.setMonthlyPaid(true);

		loanRepository.save(loan);

		return new MonthlyLoanDetailResponse(
				loan.getId(),
				loan.getMember().getName(),
				loan.getRemainingPrincipal(),
				monthlyInterest,
				principalRepayment,
				totalReceived
		);
	}

	@Transactional
	public Loan createLoan(LoanCreateRequest request) {

		// 1. Find the member
		Member member = memberRepository.findById(request.memberId())
				.orElseThrow(() ->
						new ResourceNotFoundException("Member not found")
				);

		// 2. Get current app state
		AppState appState = appStateService.getAppState();

		BigDecimal loanAmount = request.principalAmount();

		// 3. Check bank balance
		if (loanAmount.compareTo(appState.getBankBalance()) > 0) {
			throw new BadRequestException(
					"Loan amount cannot be greater than current bank balance"
			);
		}

		// 4. Check whether member already has an active loan
		var existingLoan =
				loanRepository.findByMemberIdAndStatus(
						member.getId(),
						LoanStatus.ACTIVE
				);

		Loan loan;

		if (existingLoan.isPresent()) {

			// Existing active loan → add amount to it
			loan = existingLoan.get();

			loan.setRemainingPrincipal(
					loan.getRemainingPrincipal()
							.add(loanAmount)
			);

		} else {

			// No active loan → create a new one
			loan = new Loan();

			loan.setMember(member);
			loan.setRemainingPrincipal(loanAmount);
			loan.setStatus(LoanStatus.ACTIVE);

			loan.setMonthlyPrincipalRepayment(BigDecimal.ZERO);
			loan.setMonthlyInterest(BigDecimal.ZERO);
			loan.setMonthlyTotalReceived(BigDecimal.ZERO);
			loan.setMonthlyPaid(false);
		}

		// 5. Subtract newly given loan amount from bank balance
		appState.setBankBalance(
				appState.getBankBalance()
						.subtract(loanAmount)
		);

		// 6. Save both
		loanRepository.save(loan);
		appStateRepository.save(appState);

		return loan;
	}
}
