# Security policy

LedgerGuard is a portfolio project and must not process real payment or personal
data. Report a vulnerability through a private GitHub security advisory instead
of opening a public issue.

## Local secrets

The credentials in `docker-compose.yml` are isolated development defaults. A
real deployment must inject secrets from a secret manager and enable TLS/SASL
for PostgreSQL, Kafka and service traffic.
