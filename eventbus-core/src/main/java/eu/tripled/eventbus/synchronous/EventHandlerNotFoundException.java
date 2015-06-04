package eu.tripled.eventbus.synchronous;

public class EventHandlerNotFoundException extends RuntimeException {

  public EventHandlerNotFoundException(String message) {
    super(message);
  }
}
