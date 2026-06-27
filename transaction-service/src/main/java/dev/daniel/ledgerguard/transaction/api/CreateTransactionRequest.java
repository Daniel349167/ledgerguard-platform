package dev.daniel.ledgerguard.transaction.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank @Size(max = 80) String accountExternalIdDebit,
        @NotBlank @Size(max = 80) String accountExternalIdCredit,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @NotBlank @Pattern(regexp = "[A-Za-z]{2}") String country,
        boolean newBeneficiary) {
}
