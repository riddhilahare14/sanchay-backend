package com.sanchay.monthly;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monthly")
public class MonthlyCloseController {

    private final MonthlyCloseService monthlyCloseService;

    public MonthlyCloseController(
            MonthlyCloseService monthlyCloseService
    ) {
        this.monthlyCloseService = monthlyCloseService;
    }

    @PostMapping("/preview")
    public MonthlyPreviewResponse previewMonthlyClose() {
        return monthlyCloseService.previewMonthlyClose();
    }

    @PostMapping("/close")
    public void closeMonth() {
        monthlyCloseService.closeMonth();
    }
}