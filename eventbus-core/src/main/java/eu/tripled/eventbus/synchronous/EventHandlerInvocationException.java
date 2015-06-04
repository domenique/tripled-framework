package eu.tripled.eventbus.synchronous;

public class EventHandlerInvocationException extends RuntimeException {

  public EventHandlerInvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
