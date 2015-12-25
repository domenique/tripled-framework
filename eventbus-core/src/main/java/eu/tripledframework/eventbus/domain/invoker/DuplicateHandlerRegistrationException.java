package eu.tripledframework.eventbus.domain.invoker;

public class DuplicateHandlerRegistrationException extends RuntimeException {
  public DuplicateHandlerRegistrationException(String message) {
    super(message);
  }
}
