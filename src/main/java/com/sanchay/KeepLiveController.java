package com.sanchay;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeepLiveController {

    @GetMapping("/keep-live")
    public String keepLive() {
        return "Sanchay backend is running";
    }
}