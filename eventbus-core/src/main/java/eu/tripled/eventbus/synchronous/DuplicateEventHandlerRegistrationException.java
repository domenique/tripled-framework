package eu.tripled.eventbus.synchronous;

public class DuplicateEventHandlerRegistrationException extends RuntimeException {
  public DuplicateEventHandlerRegistrationException(String message) {
    super(message);
  }
}
