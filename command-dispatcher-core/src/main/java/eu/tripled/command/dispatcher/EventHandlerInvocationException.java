package eu.tripled.command.dispatcher;

public class EventHandlerInvocationException extends RuntimeException {

  public EventHandlerInvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
