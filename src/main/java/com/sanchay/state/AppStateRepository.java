package com.sanchay.state;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppStateRepository extends JpaRepository<AppState, Long> {
}