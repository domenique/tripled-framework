package eu.tripledframework.eventbus.domain.callback;

public class CommandFailedException extends RuntimeException {

  public CommandFailedException(Throwable exception) {
    super(exception);
  }
}
