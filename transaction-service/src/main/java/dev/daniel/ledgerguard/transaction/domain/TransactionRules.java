package dev.daniel.ledgerguard.transaction.domain;

import java.math.BigDecimal;
import java.util.Currency;

public final class TransactionRules {

    private TransactionRules() {
    }

    public static void validate(
            String debitAccount,
            String creditAccount,
            BigDecimal amount,
            String currency,
            String country) {
        if (debitAccount.equalsIgnoreCase(creditAccount)) {
            throw new IllegalArgumentException("Debit and credit accounts must be different");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Currency.getInstance(currency.toUpperCase());
        if (country == null || !country.matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("Country must use an ISO 3166-1 alpha-2 code");
        }
    }
}
