package com.sanchay.state;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record BankInterestRequest(

        @NotNull
        @DecimalMin(
                value = "0.01",
                message = "Interest amount must be greater than zero"
        )
        BigDecimal amount

) {}