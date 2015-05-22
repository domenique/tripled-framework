package be.dticonsulting.command.callback;

public class CommandValidationException extends RuntimeException {

  public CommandValidationException(String message) {
    super(message);
  }
}
