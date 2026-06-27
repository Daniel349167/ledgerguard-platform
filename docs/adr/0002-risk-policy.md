# ADR 0002: Explainable fraud scoring

Status: accepted

## Context

A portfolio system must make fraud decisions reproducible and auditable without
pretending that a small ruleset is a production machine-learning model.

## Decision

Fraud evaluation is a deterministic domain policy. Each rule contributes points
and a human-readable reason. Scores at or above 70 are rejected, 40-69 require
review, and lower scores are approved.

## Consequences

- Unit tests cover every decision boundary.
- Decisions are explainable and easy to replay.
- A future model can implement the same policy port without changing messaging
  or API contracts.
