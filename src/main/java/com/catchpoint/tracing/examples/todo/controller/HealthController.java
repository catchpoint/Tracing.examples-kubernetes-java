package com.catchpoint.tracing.examples.todo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sozal
 */
@RestController
@RequestMapping("/")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

}