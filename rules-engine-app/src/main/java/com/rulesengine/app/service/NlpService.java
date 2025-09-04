package com.rulesengine.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rulesengine.model.dtos.StagingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NlpService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${nlp.service.url}")
    private String nlpServiceUrl;

    public NlpService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode parseRule(StagingRequest stagingRequest) {
        // TODO: In a real application, you would fetch the schema for the namespace
        // and include it in the request to the NLP service for context.

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("rule_text", stagingRequest.getNaturalLanguageRule());
        // requestPayload.put("schema", fetchedSchema);

        try {
            return restTemplate.postForObject(nlpServiceUrl, requestPayload, JsonNode.class);
        } catch (Exception e) {
            // Proper error handling and logging should be implemented here.
            System.err.println("Error calling NLP service: " + e.getMessage());
            // For now, return a mock response on failure to allow UI to function.
            return objectMapper.createObjectNode()
                    .put("ruleName", "MockRuleOnError")
                    .put("description", stagingRequest.getNaturalLanguageRule());
        }
    }
}

