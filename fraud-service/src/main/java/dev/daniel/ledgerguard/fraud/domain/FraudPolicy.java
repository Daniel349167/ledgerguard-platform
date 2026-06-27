package dev.daniel.ledgerguard.fraud.domain;

import dev.daniel.ledgerguard.contracts.FraudDecision;
import dev.daniel.ledgerguard.contracts.TransactionRequested;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public final class FraudPolicy {

    private static final BigDecimal LARGE_AMOUNT = new BigDecimal("5000.00");
    private static final BigDecimal VERY_LARGE_AMOUNT = new BigDecimal("10000.00");

    private final Set<String> highRiskCountries;

    public FraudPolicy(Set<String> highRiskCountries) {
        this.highRiskCountries = Set.copyOf(highRiskCountries);
    }

    public FraudDecision evaluate(TransactionRequested transaction, Instant evaluatedAt) {
        int score = 0;
        var reasons = new ArrayList<String>();

        if (transaction.amount().compareTo(VERY_LARGE_AMOUNT) >= 0) {
            score += 55;
            reasons.add("VERY_LARGE_AMOUNT");
        } else if (transaction.amount().compareTo(LARGE_AMOUNT) >= 0) {
            score += 35;
            reasons.add("LARGE_AMOUNT");
        }

        if (transaction.newBeneficiary()) {
            score += 25;
            reasons.add("NEW_BENEFICIARY");
        }

        if (highRiskCountries.contains(transaction.country().toUpperCase(Locale.ROOT))) {
            score += 45;
            reasons.add("HIGH_RISK_COUNTRY");
        }

        FraudDecision.Decision decision = score >= 70
                ? FraudDecision.Decision.REJECT
                : score >= 40
                    ? FraudDecision.Decision.REVIEW
                    : FraudDecision.Decision.APPROVE;

        if (reasons.isEmpty()) {
            reasons.add("NO_RISK_RULE_MATCHED");
        }

        return new FraudDecision(
                transaction.transactionId(),
                decision,
                score,
                reasons,
                evaluatedAt);
    }
}
