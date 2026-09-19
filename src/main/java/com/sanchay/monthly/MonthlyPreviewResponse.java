package com.sanchay.monthly;

import java.math.BigDecimal;

public record MonthlyPreviewResponse(
        BigDecimal totalHafta,
        BigDecimal totalLoanPrincipal,
        BigDecimal totalLoanInterest,
        BigDecimal totalBankDeposit
) {
}