package dev.daniel.ledgerguard.fraud.application;

import dev.daniel.ledgerguard.contracts.FraudDecision;
import dev.daniel.ledgerguard.contracts.TransactionRequested;
import dev.daniel.ledgerguard.fraud.domain.FraudPolicy;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class FraudEvaluator {

    private final FraudPolicy policy = new FraudPolicy(Set.of("IR", "KP", "SY"));

    public FraudDecision evaluate(TransactionRequested transaction) {
        return policy.evaluate(transaction, Instant.now());
    }
}
