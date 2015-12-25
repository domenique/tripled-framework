package eu.tripledframework.eventbus.domain.invoker;

public class DuplicateInvokerRegistrationException extends RuntimeException {
  public DuplicateInvokerRegistrationException(String message) {
    super(message);
  }
}
