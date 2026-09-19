package com.sanchay.loan;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LoanCreateRequest(

        @NotNull
        Long memberId,

        @NotNull
        @DecimalMin(
                value = "1.0",
                message = "Loan amount must be greater than zero"
        )
        BigDecimal principalAmount

) {}