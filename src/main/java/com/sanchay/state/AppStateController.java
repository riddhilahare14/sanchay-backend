package com.sanchay.state;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/state")
public class AppStateController {

    private final AppStateService appStateService;

    public AppStateController(AppStateService appStateService) {
        this.appStateService = appStateService;
    }

    @GetMapping
    public AppState getState() {
        return appStateService.getAppState();
    }

    @PostMapping("/bank-interest")
    public AppState addBankInterest(
            @Valid @RequestBody BankInterestRequest request
    ) {
        return appStateService.addBankInterest(request.amount());
    }
}