package com.rulesengine.app.controller;

import com.rulesengine.app.service.RuleService;
import com.rulesengine.model.entities.Rule;
import com.rulesengine.model.dtos.StagingRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "http://localhost:3000")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    // --- FIX #1: ADDED THIS METHOD ---
    // This handles the GET request from the frontend when the page loads.
    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'EDITOR', 'ADMIN')")
    public ResponseEntity<List<Rule>> getAllRules() {
        // NOTE: You will need to add a `getAllRules()` method to your RuleService class.
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    // --- FIX #2: ADDED THIS METHOD ---
    // This handles the POST request from the frontend when a new rule is saved.
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<Rule> createRule(@Valid @RequestBody Rule rule) {
        // NOTE: You will need to add a `createRule(Rule rule)` method to your RuleService class.
        return ResponseEntity.ok(ruleService.createRule(rule));
    }


    // --- Your existing methods are below ---

    @GetMapping("/{namespace}")
    @PreAuthorize("hasAnyRole('VIEWER', 'EDITOR', 'ADMIN')")
    public ResponseEntity<List<Rule>> getRules(@PathVariable String namespace) {
        return ResponseEntity.ok(ruleService.getRulesByNamespace(namespace));
    }

    @PostMapping("/stage")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<Rule> stageRule(@Valid @RequestBody StagingRequest stagingRequest) {
        return ResponseEntity.ok(ruleService.stageRule(stagingRequest));
    }

    @PostMapping("/deploy/{namespace}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deployRules(@PathVariable String namespace) {
        ruleService.deployRules(namespace);
        return ResponseEntity.ok().build();
    }
}