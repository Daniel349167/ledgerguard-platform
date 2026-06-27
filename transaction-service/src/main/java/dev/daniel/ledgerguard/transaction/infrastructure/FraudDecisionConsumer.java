package dev.daniel.ledgerguard.transaction.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daniel.ledgerguard.contracts.FraudDecision;
import dev.daniel.ledgerguard.transaction.application.TransactionApplicationService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class FraudDecisionConsumer {

    private final ObjectMapper objectMapper;
    private final TransactionApplicationService service;

    public FraudDecisionConsumer(ObjectMapper objectMapper, TransactionApplicationService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @Incoming("fraud-decisions")
    public void consume(String payload) {
        try {
            service.applyDecision(objectMapper.readValue(payload, FraudDecision.class));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid fraud decision payload", exception);
        }
    }
}
