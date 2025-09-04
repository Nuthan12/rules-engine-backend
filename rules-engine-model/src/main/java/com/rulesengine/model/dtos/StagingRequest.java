package com.rulesengine.model.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * A Data Transfer Object (DTO) that represents the request to stage a new rule.
 * It carries the raw natural language text and the namespace it belongs to from the frontend to the backend.
 */
@Data
public class StagingRequest {

    /**
     * The plain English rule text entered by the user in the UI.
     * e.g., "If the PO Number contains 'TBC', show error PO01."
     */
    @NotBlank
    private String naturalLanguageRule;

    /**
     * The namespace (or context) this rule applies to.
     * e.g., "cart_amendment"
     */
    @NotBlank
    private String namespace;
}

