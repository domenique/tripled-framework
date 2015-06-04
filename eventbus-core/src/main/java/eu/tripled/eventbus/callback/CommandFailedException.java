package eu.tripled.eventbus.callback;

public class CommandFailedException extends RuntimeException {

  public CommandFailedException(Throwable exception) {
    super(exception);
  }
}
