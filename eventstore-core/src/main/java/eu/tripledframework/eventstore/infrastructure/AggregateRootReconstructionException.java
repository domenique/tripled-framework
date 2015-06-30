package eu.tripledframework.eventstore.infrastructure;

public class AggregateRootReconstructionException extends RuntimeException {

    public AggregateRootReconstructionException(String message) {
        super(message);
    }

    public AggregateRootReconstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
