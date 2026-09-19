package com.sanchay.monthly;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanchay.exception.BadRequestException;
import com.sanchay.loan.Loan;
import com.sanchay.loan.LoanRepository;
import com.sanchay.loan.LoanStatus;
import com.sanchay.member.Member;
import com.sanchay.member.MemberRepository;
import com.sanchay.state.AppState;
import com.sanchay.state.AppStateRepository;
import com.sanchay.state.AppStateService;

@Service
public class MonthlyCloseService {

    private final AppStateRepository appStateRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final AppStateService appStateService;

    public MonthlyCloseService(
            AppStateRepository appStateRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository,
            AppStateService appStateService
    ) {
        this.appStateRepository = appStateRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.appStateService = appStateService;
    }

    private void validateMembers() {

        long totalMembers = memberRepository.count();

        long paidMembers = memberRepository.findAll()
                .stream()
                .filter(Member::isMonthlyPaid)
                .count();

        if (paidMembers != totalMembers) {
            throw new BadRequestException(
                    "All members must pay their monthly contribution before closing the month"
            );
        }
    }

    private void validateLoans() {

        long activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE)
                .size();

        long paidLoans = loanRepository.findByStatus(LoanStatus.ACTIVE)
                .stream()
                .filter(Loan::isMonthlyPaid)
                .count();

        if (paidLoans != activeLoans) {
            throw new BadRequestException(
                    "All active loans must be paid before closing the month"
            );
        }
    }

    public MonthlyPreviewResponse previewMonthlyClose() {

        validateMembers();
        validateLoans();

        AppState appState = appStateService.getAppState();

        long memberCount = memberRepository.count();

        BigDecimal totalHafta =
                appState.getMonthlyContribution()
                        .multiply(BigDecimal.valueOf(memberCount));

        BigDecimal totalLoanPrincipal =
                loanRepository.findByStatus(LoanStatus.ACTIVE)
                        .stream()
                        .map(Loan::getMonthlyPrincipalRepayment)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLoanInterest =
                loanRepository.findByStatus(LoanStatus.ACTIVE)
                        .stream()
                        .map(Loan::getMonthlyInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBankDeposit =
                totalHafta
                        .add(totalLoanPrincipal)
                        .add(totalLoanInterest);

        return new MonthlyPreviewResponse(
                totalHafta,
                totalLoanPrincipal,
                totalLoanInterest,
                totalBankDeposit
        );
    }

    @Transactional
    public void closeMonth() {

        validateMembers();
        validateLoans();

        AppState appState =
                appStateService.getAppState();

        long memberCount =
                memberRepository.count();

        BigDecimal totalHafta =
                appState.getMonthlyContribution()
                        .multiply(BigDecimal.valueOf(memberCount));

        BigDecimal totalLoanPrincipal =
                loanRepository.findByStatus(LoanStatus.ACTIVE)
                        .stream()
                        .map(Loan::getMonthlyPrincipalRepayment)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLoanInterest =
                loanRepository.findByStatus(LoanStatus.ACTIVE)
                        .stream()
                        .map(Loan::getMonthlyInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBankDeposit =
                totalHafta
                        .add(totalLoanPrincipal)
                        .add(totalLoanInterest);

        // Update bank balance
        appState.setBankBalance(
                appState.getBankBalance()
                        .add(totalBankDeposit)
        );

        // Update total savings
        appState.setTotalSavings(
                appState.getTotalSavings()
                        .add(totalHafta)
        );

        // Update savings including interest
        appState.setTotalSavingsWithInterest(
                appState.getTotalSavingsWithInterest()
                        .add(totalHafta)
                        .add(totalLoanInterest)
        );

        /*
         * Apply loan principal repayments
         */
        loanRepository.findByStatus(LoanStatus.ACTIVE)
                .stream()
                .forEach(loan -> {

                    BigDecimal newRemainingPrincipal =
                            loan.getRemainingPrincipal()
                                    .subtract(
                                            loan.getMonthlyPrincipalRepayment()
                                    );

                    loan.setRemainingPrincipal(
                            newRemainingPrincipal
                    );

                    if (newRemainingPrincipal.compareTo(
                            BigDecimal.ZERO
                    ) == 0) {

                        loan.setStatus(LoanStatus.NIL);
                    }

                    // Reset current month's loan state
                    loan.setMonthlyPrincipalRepayment(
                            BigDecimal.ZERO
                    );

                    loan.setMonthlyInterest(
                            BigDecimal.ZERO
                    );

                    loan.setMonthlyTotalReceived(
                            BigDecimal.ZERO
                    );

                    loan.setMonthlyPaid(false);

                    loanRepository.save(loan);
                });

        /*
         * Reset member monthly payments
         */
        memberRepository.findAll()
                .forEach(member -> {

                    member.setMonthlyPaid(false);

                    memberRepository.save(member);
                });

        /*
         * Save application state
         */
        appStateRepository.save(appState);
    }
}