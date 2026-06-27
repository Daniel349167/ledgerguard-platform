package dev.daniel.ledgerguard.transaction.infrastructure;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Page;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Instant;
import java.util.List;

import static io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP;

@ApplicationScoped
public class OutboxPublisher {

    @Channel("transactions-out")
    Emitter<String> emitter;

    @Scheduled(every = "1s", concurrentExecution = SKIP)
    @Transactional
    void publishBatch() {
        List<OutboxEventEntity> events = OutboxEventEntity
                .<OutboxEventEntity>find("publishedAt is null order by createdAt")
                .page(Page.ofSize(50))
                .list();

        for (var event : events) {
            emitter.send(event.payload).toCompletableFuture().join();
            event.publishedAt = Instant.now();
        }
        Panache.getEntityManager().flush();
    }
}
