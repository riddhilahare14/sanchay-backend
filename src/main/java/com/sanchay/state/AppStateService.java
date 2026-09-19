package com.sanchay.state;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanchay.exception.BadRequestException;
import com.sanchay.exception.ResourceNotFoundException;

@Service
public class AppStateService {

    private final AppStateRepository appStateRepository;

    public AppStateService(AppStateRepository appStateRepository) {
        this.appStateRepository = appStateRepository;
    }

    public AppState getAppState() {
        return appStateRepository.findById(1L)
                .orElseThrow(() ->
                        new ResourceNotFoundException("App state not found")
                );
    }

    @Transactional
    public AppState addBankInterest(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Interest amount must be greater than zero"
            );
        }

        AppState appState = getAppState();

        appState.setBankBalance(
                appState.getBankBalance().add(amount)
        );

        appState.setTotalSavingsWithInterest(
                appState.getTotalSavingsWithInterest().add(amount)
        );

        return appStateRepository.save(appState);
    }
}