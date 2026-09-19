package com.sanchay.loan;

import java.math.BigDecimal;

public record ActiveLoanResponse(
        Long loanId,
        String memberName,
        BigDecimal remainingPrincipal,
        BigDecimal monthlyInterest,
        BigDecimal monthlyPrincipalRepayment,
        BigDecimal monthlyTotalReceived,
        boolean monthlyPaid
) {}