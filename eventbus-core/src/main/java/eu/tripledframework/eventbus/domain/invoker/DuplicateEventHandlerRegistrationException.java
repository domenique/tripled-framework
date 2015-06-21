package eu.tripledframework.eventbus.domain.invoker;

public class DuplicateEventHandlerRegistrationException extends RuntimeException {
  public DuplicateEventHandlerRegistrationException(String message) {
    super(message);
  }
}
