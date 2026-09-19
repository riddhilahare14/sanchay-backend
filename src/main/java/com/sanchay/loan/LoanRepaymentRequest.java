package com.sanchay.loan;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record LoanRepaymentRequest(

        @NotNull(message = "Loan ID is required")
        Long loanId,

        @NotNull(message = "Principal repayment is required")
        @PositiveOrZero(message = "Principal repayment cannot be negative")
        BigDecimal principalRepayment
) {
}