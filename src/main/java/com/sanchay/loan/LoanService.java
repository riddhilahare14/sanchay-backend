package com.sanchay.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanchay.exception.BadRequestException;
import com.sanchay.exception.ResourceNotFoundException;
import com.sanchay.state.AppStateService;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final AppStateService appStateService;

    public LoanService(
            LoanRepository loanRepository,
            AppStateService appStateService
    ) {
        this.loanRepository = loanRepository;
        this.appStateService = appStateService;
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
}
