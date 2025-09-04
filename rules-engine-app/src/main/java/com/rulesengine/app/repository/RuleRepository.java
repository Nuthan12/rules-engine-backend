package com.rulesengine.app.repository;

import com.rulesengine.model.entities.Rule;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RuleRepository extends MongoRepository<Rule, String> {

    /**
     * Finds all rules that belong to a specific namespace.
     * @param namespace The namespace to search for.
     * @return A list of rules.
     */
    List<Rule> findByNamespace(String namespace);

    /**
     * Finds all rules that belong to a specific namespace and have a specific status.
     * This will be used to fetch only DEPLOYED rules for the Drools engine.
     * @param namespace The namespace to search for.
     * @param status The status to filter by (e.g., "DEPLOYED").
     * @return A list of rules.
     */
    List<Rule> findByNamespaceAndStatus(String namespace, String status);
}

