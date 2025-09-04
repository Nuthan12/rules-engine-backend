package com.rulesengine.drools.service;

import com.rulesengine.model.entities.Rule;
import org.drools.io.ReaderResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The core service for interacting with the Drools engine.
 * This service is the "engine room" of our application. It is responsible for:
 * 1. Generating DRL (Drools Rule Language) strings from our Rule objects.
 * 2. Compiling the DRL into an executable KieContainer.
 * 3. Executing input data against the compiled rules.
 */
@Service
public class DroolsService {

    private final KieServices kieServices = KieServices.Factory.get();

    /**
     * A thread-safe map to hold the compiled rule containers for each namespace.
     * The key is the namespace (e.g., "cart_amendment"), and the value is the compiled
     * set of rules for that namespace. This is crucial for our multi-context design.
     */
    private final Map<String, KieContainer> kieContainerMap = new ConcurrentHashMap<>();

    /**
     * Takes a list of Rule objects for a specific namespace, generates DRL,
     * compiles it, and updates the KieContainer for that namespace.
     * This method is called whenever rules are deployed.
     *
     * @param namespace The context the rules belong to (e.g., "cart_amendment").
     * @param rules     The list of DEPLOYED rules from MongoDB for that namespace.
     */
    public void updateRules(String namespace, List<Rule> rules) {
        String drlString = generateDrlString(rules);
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write("src/main/resources/rules/" + namespace + ".drl", new ReaderResource(new StringReader(drlString)));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            // In a real application, you would use a proper logger (e.g., SLF4J)
            System.err.println("DRL Compilation errors: " + kieBuilder.getResults().getMessages());
            throw new IllegalStateException("Could not compile DRL for namespace: " + namespace);
        }

        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        kieContainerMap.put(namespace, kieContainer);
        System.out.println("Successfully compiled and loaded " + rules.size() + " rules for namespace: " + namespace);
    }

    /**
     * Executes the compiled rules for a given namespace against a set of input data.
     *
     * @param namespace The context to execute against.
     * @param inputData A map representing the dynamic request data (e.g., the cart_request).
     * @return The modified map after rules have been fired.
     */
    public Map<String, Object> executeRules(String namespace, Map<String, Object> inputData) {
        KieContainer kieContainer = kieContainerMap.get(namespace);
        if (kieContainer == null) {
            System.err.println("No rules loaded for namespace: " + namespace + ". Please deploy rules first.");
            // In a real app, you might throw a specific exception here.
            return inputData;
        }

        KieSession kieSession = kieContainer.newKieSession();

        // We will also add a list to hold validation results.
        // List<String> validationErrors = new ArrayList<>();
        // kieSession.setGlobal("validationErrors", validationErrors);

        kieSession.insert(inputData);
        kieSession.fireAllRules();
        kieSession.dispose();

        // The inputData map may have been modified by the rules.
        return inputData;
    }

    /**
     * Generates a single, valid DRL file string from a list of Rule objects.
     * This is a critical translation step.
     *
     * @param rules The list of rules to convert.
     * @return A DRL string.
     */
    private String generateDrlString(List<Rule> rules) {
        // TODO: This is a placeholder. In the next steps, we will implement a robust
        // DRL generation logic using Drools Templates. This will iterate through each
        // Rule object and create a corresponding "rule ... when ... then ..." block.

        StringBuilder drlBuilder = new StringBuilder();
        drlBuilder.append("package com.rulesengine.generated;\n\n");
        drlBuilder.append("import java.util.Map;\n\n");

        for (Rule rule : rules) {
            drlBuilder.append("// Rule: ").append(rule.getRuleName()).append("\n");
            // Placeholder for actual rule logic
        }

        return drlBuilder.toString();
    }
}

