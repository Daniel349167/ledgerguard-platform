package dev.daniel.ledgerguard.transaction.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daniel.ledgerguard.contracts.FraudDecision;
import dev.daniel.ledgerguard.contracts.TransactionRequested;
import dev.daniel.ledgerguard.transaction.api.CreateTransactionRequest;
import dev.daniel.ledgerguard.transaction.api.TransactionResponse;
import dev.daniel.ledgerguard.transaction.domain.TransactionRules;
import dev.daniel.ledgerguard.transaction.domain.TransactionStatus;
import dev.daniel.ledgerguard.transaction.infrastructure.OutboxEventEntity;
import dev.daniel.ledgerguard.transaction.infrastructure.TransactionEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TransactionApplicationService {

    private final ObjectMapper objectMapper;

    public TransactionApplicationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TransactionResponse create(String idempotencyKey, CreateTransactionRequest request) {
        var existing = TransactionEntity.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        TransactionRules.validate(
                request.accountExternalIdDebit(),
                request.accountExternalIdCredit(),
                request.amount(),
                request.currency(),
                request.country());

        var now = Instant.now();
        var transaction = new TransactionEntity();
        transaction.id = UUID.randomUUID();
        transaction.idempotencyKey = idempotencyKey;
        transaction.accountExternalIdDebit = request.accountExternalIdDebit();
        transaction.accountExternalIdCredit = request.accountExternalIdCredit();
        transaction.amount = request.amount();
        transaction.currency = request.currency().toUpperCase();
        transaction.country = request.country().toUpperCase();
        transaction.newBeneficiary = request.newBeneficiary();
        transaction.status = TransactionStatus.PENDING;
        transaction.createdAt = now;
        transaction.updatedAt = now;
        transaction.persist();

        var event = new TransactionRequested(
                transaction.id,
                transaction.accountExternalIdDebit,
                transaction.accountExternalIdCredit,
                transaction.amount,
                transaction.currency,
                transaction.country,
                transaction.newBeneficiary,
                now);

        var outbox = new OutboxEventEntity();
        outbox.id = UUID.randomUUID();
        outbox.aggregateId = transaction.id;
        outbox.eventType = "transaction.requested.v1";
        outbox.payload = writeJson(event);
        outbox.createdAt = now;
        outbox.persist();

        return toResponse(transaction);
    }

    public TransactionResponse find(UUID id) {
        var transaction = TransactionEntity.<TransactionEntity>findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
        return toResponse(transaction);
    }

    @Transactional
    public void applyDecision(FraudDecision decision) {
        var transaction = TransactionEntity.<TransactionEntity>findByIdOptional(decision.transactionId())
                .orElseThrow(NotFoundException::new);
        transaction.status = switch (decision.decision()) {
            case APPROVE -> TransactionStatus.APPROVED;
            case REVIEW -> TransactionStatus.UNDER_REVIEW;
            case REJECT -> TransactionStatus.REJECTED;
        };
        transaction.riskScore = decision.riskScore();
        transaction.decisionReasons = String.join("|", decision.reasons());
        transaction.updatedAt = Instant.now();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize outbox event", exception);
        }
    }

    private TransactionResponse toResponse(TransactionEntity entity) {
        List<String> reasons = entity.decisionReasons == null || entity.decisionReasons.isBlank()
                ? List.of()
                : Arrays.asList(entity.decisionReasons.split("\\|"));
        return new TransactionResponse(
                entity.id,
                entity.accountExternalIdDebit,
                entity.accountExternalIdCredit,
                entity.amount,
                entity.currency,
                entity.country,
                entity.status,
                entity.riskScore,
                reasons,
                entity.createdAt);
    }
}
