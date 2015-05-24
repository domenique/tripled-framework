package eu.tripled.command.callback;

public class CommandValidationException extends RuntimeException {

  public CommandValidationException(String message) {
    super(message);
  }
}
