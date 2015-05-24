package eu.tripled.command.callback;

public class CommandFailedException extends RuntimeException {

  public CommandFailedException(Throwable exception) {
    super(exception);
  }
}
