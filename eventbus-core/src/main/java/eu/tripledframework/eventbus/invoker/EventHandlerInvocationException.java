package eu.tripledframework.eventbus.invoker;

public class EventHandlerInvocationException extends RuntimeException {

  public EventHandlerInvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
