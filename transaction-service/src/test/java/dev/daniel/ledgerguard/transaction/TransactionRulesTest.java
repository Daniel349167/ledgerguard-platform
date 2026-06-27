package dev.daniel.ledgerguard.transaction;

import dev.daniel.ledgerguard.transaction.domain.TransactionRules;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionRulesTest {

    @Test
    void acceptsAValidTransfer() {
        assertDoesNotThrow(() -> TransactionRules.validate(
                "ACC-100", "ACC-200", new BigDecimal("25.50"), "PEN", "PE"));
    }

    @Test
    void rejectsTransfersToTheSameAccount() {
        assertThrows(IllegalArgumentException.class, () -> TransactionRules.validate(
                "ACC-100", "acc-100", BigDecimal.ONE, "USD", "US"));
    }

    @Test
    void rejectsUnknownCurrencies() {
        assertThrows(IllegalArgumentException.class, () -> TransactionRules.validate(
                "ACC-100", "ACC-200", BigDecimal.ONE, "ZZZ", "PE"));
    }
}
