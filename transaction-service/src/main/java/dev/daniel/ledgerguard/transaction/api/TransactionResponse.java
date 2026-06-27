package dev.daniel.ledgerguard.transaction.api;

import dev.daniel.ledgerguard.transaction.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        String accountExternalIdDebit,
        String accountExternalIdCredit,
        BigDecimal amount,
        String currency,
        String country,
        TransactionStatus status,
        Integer riskScore,
        List<String> decisionReasons,
        Instant createdAt) {
}
