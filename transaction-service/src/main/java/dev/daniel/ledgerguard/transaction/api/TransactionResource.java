package dev.daniel.ledgerguard.transaction.api;

import dev.daniel.ledgerguard.transaction.application.TransactionApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.UUID;

@Path("/api/v1/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private final TransactionApplicationService service;

    public TransactionResource(TransactionApplicationService service) {
        this.service = service;
    }

    @POST
    public Response create(
            @HeaderParam("Idempotency-Key") @NotBlank String idempotencyKey,
            @Valid CreateTransactionRequest request) {
        var response = service.create(idempotencyKey, request);
        return Response.accepted(response)
                .location(URI.create("/api/v1/transactions/" + response.transactionId()))
                .build();
    }

    @GET
    @Path("/{id}")
    public TransactionResponse find(@PathParam("id") UUID id) {
        return service.find(id);
    }
}
