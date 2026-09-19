package com.sanchay.loan;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MonthlyLoanRequest(

        @NotNull(message = "Repayments are required")
        @Valid
        List<LoanRepaymentRequest> repayments
) {
}