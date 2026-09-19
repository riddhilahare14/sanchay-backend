package com.sanchay.state;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_state")
@Getter
@Setter
@NoArgsConstructor
public class AppState {

    @Id
    private Long id;

    @Column(name = "monthly_contribution", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyContribution;

    @Column(name = "loan_interest_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal loanInterestRate;

    @Column(name = "total_savings", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalSavings;

    @Column(name = "total_savings_with_interest", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalSavingsWithInterest;

    @Column(name = "bank_balance", nullable = false, precision = 14, scale = 2)
    private BigDecimal bankBalance;
}