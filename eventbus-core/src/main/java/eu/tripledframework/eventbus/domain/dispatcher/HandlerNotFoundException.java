package eu.tripledframework.eventbus.domain.dispatcher;

public class HandlerNotFoundException extends RuntimeException {

  public HandlerNotFoundException(String message) {
    super(message);
  }
}
