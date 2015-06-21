package eu.tripledframework.eventbus.domain.invoker;

public class EventHandlerInvocationException extends RuntimeException {

  public EventHandlerInvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
