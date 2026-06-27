package dev.daniel.ledgerguard.fraud.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daniel.ledgerguard.contracts.TransactionRequested;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class TransactionRequestedConsumer {

    private final ObjectMapper objectMapper;
    private final FraudEvaluator evaluator;

    @Channel("fraud-out")
    Emitter<String> emitter;

    public TransactionRequestedConsumer(ObjectMapper objectMapper, FraudEvaluator evaluator) {
        this.objectMapper = objectMapper;
        this.evaluator = evaluator;
    }

    @Incoming("transactions-in")
    public void consume(String payload) {
        try {
            var request = objectMapper.readValue(payload, TransactionRequested.class);
            var decision = evaluator.evaluate(request);
            emitter.send(objectMapper.writeValueAsString(decision)).toCompletableFuture().join();
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid transaction event payload", exception);
        }
    }
}
