package eu.tripledframework.eventbus.internal.infrastructure.invoker;

public class DuplicateInvokerRegistrationException extends RuntimeException {
  public DuplicateInvokerRegistrationException(String message) {
    super(message);
  }
}
