package dev.daniel.ledgerguard.contracts;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRequested(
        UUID transactionId,
        String accountExternalIdDebit,
        String accountExternalIdCredit,
        BigDecimal amount,
        String currency,
        String country,
        boolean newBeneficiary,
        Instant occurredAt) {
}
