package dev.daniel.ledgerguard.transaction.infrastructure;

import dev.daniel.ledgerguard.transaction.domain.TransactionStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    public String idempotencyKey;

    @Column(name = "account_debit", nullable = false)
    public String accountExternalIdDebit;

    @Column(name = "account_credit", nullable = false)
    public String accountExternalIdCredit;

    @Column(nullable = false, precision = 19, scale = 2)
    public BigDecimal amount;

    @Column(nullable = false, length = 3)
    public String currency;

    @Column(nullable = false, length = 2)
    public String country;

    @Column(name = "new_beneficiary", nullable = false)
    public boolean newBeneficiary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TransactionStatus status;

    @Column(name = "risk_score")
    public Integer riskScore;

    @Column(name = "decision_reasons")
    public String decisionReasons;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @Version
    public long version;

    public static Optional<TransactionEntity> findByIdempotencyKey(String idempotencyKey) {
        return find("idempotencyKey", idempotencyKey).firstResultOptional();
    }
}
