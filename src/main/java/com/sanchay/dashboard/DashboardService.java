package com.sanchay.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanchay.loan.Loan;
import com.sanchay.loan.LoanRepository;
import com.sanchay.loan.LoanStatus;
import com.sanchay.member.MemberRepository;
import com.sanchay.state.AppState;
import com.sanchay.state.AppStateService;

@Service
public class DashboardService {

    private final AppStateService appStateService;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public DashboardService(
            AppStateService appStateService,
            MemberRepository memberRepository,
            LoanRepository loanRepository
    ) {
        this.appStateService = appStateService;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public DashboardResponse getDashboard() {

        AppState appState = appStateService.getAppState();

        long memberCount = memberRepository.count();

        BigDecimal memberShare = BigDecimal.ZERO;

        if (memberCount > 0) {
            memberShare = appState.getTotalSavingsWithInterest()
                    .divide(
                            BigDecimal.valueOf(memberCount),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        List<Loan> activeLoans =
                loanRepository.findByStatus(LoanStatus.ACTIVE);

        BigDecimal outstandingLoans = activeLoans.stream()
                .map(Loan::getRemainingPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMonthlyHafta =
                appState.getMonthlyContribution()
                        .multiply(BigDecimal.valueOf(memberCount));

        return new DashboardResponse(
                appState.getTotalSavings(),
                appState.getTotalSavingsWithInterest(),
                memberShare,
                appState.getBankBalance(),
                outstandingLoans,
                appState.getMonthlyContribution(),
                totalMonthlyHafta,
                memberCount
        );
    }
}