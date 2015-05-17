package be.dticonsulting.support.command.application.callback;

public class CommandValidationException extends RuntimeException {

  public CommandValidationException(String message) {
    super(message);
  }
}
