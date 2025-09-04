package com.rulesengine.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rulesengine.app.repository.RuleRepository;
import com.rulesengine.drools.service.DroolsService;
import com.rulesengine.model.entities.Rule;
import com.rulesengine.model.dtos.StagingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final NlpService nlpService;
    private final DroolsService droolsService;
    private final ObjectMapper objectMapper;

    public RuleService(RuleRepository ruleRepository, NlpService nlpService, DroolsService droolsService, ObjectMapper objectMapper) {
        this.ruleRepository = ruleRepository;
        this.nlpService = nlpService;
        this.droolsService = droolsService;
        this.objectMapper = objectMapper;
    }

    // --- NEW METHOD #1 ---
    // This method was added to support the GET /api/rules endpoint.
    // It fetches all rules from the database.
    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    // --- NEW METHOD #2 ---
    // This method was added to support the POST /api/rules endpoint.
    // It saves a new Rule object sent from the frontend form.
    public Rule createRule(Rule rule) {
        // Set default status for a newly created rule, making it ready for deployment.
        rule.setStatus("STAGED");
        // You might want to add other default fields if necessary, e.g., a default namespace.
        // if (rule.getNamespace() == null || rule.getNamespace().isEmpty()) {
        //     rule.setNamespace("default");
        // }
        rule.setLegacyRuleId("NEW"); // Maintain consistency with your stageRule method.

        return ruleRepository.save(rule);
    }


    // --- Your existing methods are below ---

    public List<Rule> getRulesByNamespace(String namespace) {
        return ruleRepository.findByNamespace(namespace);
    }

    public Rule stageRule(StagingRequest stagingRequest) {
        JsonNode parsedRuleJson = nlpService.parseRule(stagingRequest);

        // Convert the JSON from the NLP service into our Rule object
        Rule rule = objectMapper.convertValue(parsedRuleJson, Rule.class);
        rule.setNamespace(stagingRequest.getNamespace());
        rule.setStatus("STAGED");
        // In a real app, other fields like feature, tags, etc., would be set here.
        rule.setLegacyRuleId("NEW");

        return ruleRepository.save(rule);
    }

    public void deployRules(String namespace) {
        // 1. Find all STAGED rules for the namespace
        List<Rule> stagedRules = ruleRepository.findByNamespaceAndStatus(namespace, "STAGED");
        if (stagedRules.isEmpty()) {
            System.out.println("No staged rules to deploy for namespace: " + namespace);
            return;
        }

        // 2. Update their status to DEPLOYED
        stagedRules.forEach(rule -> rule.setStatus("DEPLOYED"));
        ruleRepository.saveAll(stagedRules);

        // 3. Get ALL deployed rules for the namespace
        List<Rule> allDeployedRules = ruleRepository.findByNamespaceAndStatus(namespace, "DEPLOYED");

        // 4. Update the Drools engine with the complete new set of rules
        droolsService.updateRules(namespace, allDeployedRules);
    }
}
