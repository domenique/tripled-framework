package eu.tripled.eventbus.dispatcher;

public class EventHandlerNotFoundException extends RuntimeException {

  public EventHandlerNotFoundException(String message) {
    super(message);
  }
}
