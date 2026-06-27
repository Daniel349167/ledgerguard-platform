# ADR 0001: Transactional outbox for transaction events

Status: accepted

## Context

Persisting a transaction and publishing its Kafka event as two independent
operations creates a dual-write failure mode. Either operation can succeed while
the other fails.

## Decision

The transaction service writes the transaction and an outbox record in the same
database transaction. A scheduled publisher sends unpublished records to Kafka
and marks them as published. Consumers remain idempotent because Kafka delivery
is at least once.

## Consequences

- Database state and event intent are committed atomically.
- Event publication is eventually consistent.
- A production deployment should add claim/lease semantics when multiple
  publisher replicas are active.
