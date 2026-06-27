package dev.daniel.ledgerguard.fraud;

import dev.daniel.ledgerguard.contracts.FraudDecision;
import dev.daniel.ledgerguard.contracts.TransactionRequested;
import dev.daniel.ledgerguard.fraud.domain.FraudPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudPolicyTest {

    private static final Instant NOW = Instant.parse("2026-06-27T12:00:00Z");
    private final FraudPolicy policy = new FraudPolicy(Set.of("IR", "KP", "SY"));

    @Test
    void approvesLowRiskTransactions() {
        var result = policy.evaluate(transaction("125.50", "PE", false), NOW);

        assertEquals(FraudDecision.Decision.APPROVE, result.decision());
        assertEquals(0, result.riskScore());
        assertEquals("NO_RISK_RULE_MATCHED", result.reasons().get(0));
    }

    @Test
    void keepsASingleLargeAmountSignalBelowTheReviewThreshold() {
        var result = policy.evaluate(transaction("6000.00", "PE", false), NOW);

        assertEquals(FraudDecision.Decision.APPROVE, result.decision());
        assertEquals(35, result.riskScore());
    }

    @Test
    void rejectsCombinedRiskSignals() {
        var result = policy.evaluate(transaction("12000.00", "IR", true), NOW);

        assertEquals(FraudDecision.Decision.REJECT, result.decision());
        assertEquals(125, result.riskScore());
        assertTrue(result.reasons().contains("HIGH_RISK_COUNTRY"));
    }

    @Test
    void routesMediumRiskTransactionsToReview() {
        var result = policy.evaluate(transaction("100.00", "IR", false), NOW);

        assertEquals(FraudDecision.Decision.REVIEW, result.decision());
        assertEquals(45, result.riskScore());
    }

    private TransactionRequested transaction(String amount, String country, boolean newBeneficiary) {
        return new TransactionRequested(
                UUID.fromString("0b5dd894-f345-4e50-9528-36075a98884c"),
                "ACC-100",
                "ACC-200",
                new BigDecimal(amount),
                "USD",
                country,
                newBeneficiary,
                NOW);
    }
}
