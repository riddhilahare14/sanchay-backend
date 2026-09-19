package com.sanchay.dashboard;

import java.math.BigDecimal;

public record DashboardResponse(
        BigDecimal totalSavings,
        BigDecimal totalSavingsWithInterest,
        BigDecimal memberShare,
        BigDecimal bankBalance,
        BigDecimal outstandingLoans,
        BigDecimal monthlyContribution,
        BigDecimal totalMonthlyHafta,
        long memberCount
) {
}