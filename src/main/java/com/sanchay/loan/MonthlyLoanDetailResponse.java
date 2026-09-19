package com.sanchay.loan;

import java.math.BigDecimal;

public record MonthlyLoanDetailResponse(
        Long loanId,
        String memberName,
        BigDecimal remainingPrincipal,
        BigDecimal monthlyInterest,
        BigDecimal principalRepayment,
        BigDecimal totalReceived
) {
}