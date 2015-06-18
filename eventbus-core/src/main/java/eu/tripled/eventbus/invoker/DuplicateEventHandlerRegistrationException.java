package eu.tripled.eventbus.invoker;

public class DuplicateEventHandlerRegistrationException extends RuntimeException {
  public DuplicateEventHandlerRegistrationException(String message) {
    super(message);
  }
}
