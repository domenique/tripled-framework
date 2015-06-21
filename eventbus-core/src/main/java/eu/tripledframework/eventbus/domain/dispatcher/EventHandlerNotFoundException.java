package eu.tripledframework.eventbus.domain.dispatcher;

public class EventHandlerNotFoundException extends RuntimeException {

  public EventHandlerNotFoundException(String message) {
    super(message);
  }
}
