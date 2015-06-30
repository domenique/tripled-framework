package eu.tripledframework.eventstore.domain;

import org.joda.time.DateTime;

import java.util.Objects;
import java.util.UUID;

public class DomainEvent {

    private String id;
    private String aggregateRootIdentifier;
    private DateTime timestamp;

    public DomainEvent(String aggregateRootIdentifier) {
        this.id = UUID.randomUUID().toString();
        this.aggregateRootIdentifier = aggregateRootIdentifier;
        this.timestamp = DateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getAggregateRootIdentifier() {
        return aggregateRootIdentifier;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DomainEvent other = (DomainEvent) obj;
        return Objects.equals(this.id, other.id);
    }

    protected DomainEvent() {
        // for frameworks
    }
}
