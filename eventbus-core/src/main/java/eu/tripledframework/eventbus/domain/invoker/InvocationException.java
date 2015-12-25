package eu.tripledframework.eventbus.domain.invoker;

public class InvocationException extends RuntimeException {

  public InvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
