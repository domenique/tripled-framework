package eu.tripled.command.dispatcher;

public class EventHandlerNotFoundException extends RuntimeException {

  public EventHandlerNotFoundException(String message) {
    super(message);
  }
}
