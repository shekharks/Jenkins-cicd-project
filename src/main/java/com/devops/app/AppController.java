package com.devops.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    private CalculatorService calculatorService;

    @GetMapping("/")
    public String home() {
        return "Jenkins CI/CD Pipeline - App is running! Build successful.";
    }

    @GetMapping("/add")
    public String add(@RequestParam int a, @RequestParam int b) {
        int result = calculatorService.add(a, b);
        return "Result: " + a + " + " + b + " = " + result;
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
