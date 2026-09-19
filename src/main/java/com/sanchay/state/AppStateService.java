package com.sanchay.state;

import org.springframework.stereotype.Service;

import com.sanchay.exception.ResourceNotFoundException;

@Service
public class AppStateService {

    private final AppStateRepository appStateRepository;

    public AppStateService(AppStateRepository appStateRepository) {
        this.appStateRepository = appStateRepository;
    }

    public AppState getAppState() {
        return appStateRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("App state not found"));
    }
}