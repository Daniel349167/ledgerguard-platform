package dev.daniel.ledgerguard.transaction.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "aggregate_id", nullable = false)
    public UUID aggregateId;

    @Column(name = "event_type", nullable = false)
    public String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String payload;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "published_at")
    public Instant publishedAt;
}
