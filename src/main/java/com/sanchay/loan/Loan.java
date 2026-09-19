package com.sanchay.loan;

import java.math.BigDecimal;

import com.sanchay.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "remaining_principal", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingPrincipal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @Column(name = "monthly_principal_repayment", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPrincipalRepayment;

    @Column(name = "monthly_interest", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyInterest;

    @Column(name = "monthly_total_received", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyTotalReceived;

    @Column(name = "monthly_paid", nullable = false)
    private boolean monthlyPaid;
}