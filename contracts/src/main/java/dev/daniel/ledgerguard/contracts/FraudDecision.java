package dev.daniel.ledgerguard.contracts;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudDecision(
        UUID transactionId,
        Decision decision,
        int riskScore,
        List<String> reasons,
        Instant evaluatedAt) {

    public FraudDecision {
        reasons = List.copyOf(reasons);
    }

    public enum Decision {
        APPROVE,
        REVIEW,
        REJECT
    }
}
