package com.sanchay.loan;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanchay.exception.BadRequestException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/active")
    public List<ActiveLoanResponse> getActiveLoans() {
        return loanService.getActiveLoans();
    }

    @PatchMapping("/{id}/monthly-paid")
    public MonthlyLoanDetailResponse recordLoanPayment(
            @PathVariable Long id,
            @Valid @RequestBody LoanRepaymentRequest request
    ) {
        if (!id.equals(request.loanId())) {
            throw new BadRequestException("Loan ID does not match");
        }

        return loanService.recordLoanPayment(request);
    }
}