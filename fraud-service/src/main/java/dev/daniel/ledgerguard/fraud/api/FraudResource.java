package dev.daniel.ledgerguard.fraud.api;

import dev.daniel.ledgerguard.contracts.FraudDecision;
import dev.daniel.ledgerguard.contracts.TransactionRequested;
import dev.daniel.ledgerguard.fraud.application.FraudEvaluator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/fraud/evaluate")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FraudResource {

    private final FraudEvaluator evaluator;

    public FraudResource(FraudEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @POST
    public FraudDecision evaluate(TransactionRequested transaction) {
        return evaluator.evaluate(transaction);
    }
}
