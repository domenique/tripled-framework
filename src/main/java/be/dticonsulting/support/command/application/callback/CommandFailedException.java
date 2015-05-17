package be.dticonsulting.support.command.application.callback;

public class CommandFailedException extends RuntimeException {

  public CommandFailedException(Throwable exception) {
    super(exception);
  }
}
