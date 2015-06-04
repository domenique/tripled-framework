package eu.tripled.eventbus.callback;

public class CommandValidationException extends RuntimeException {

  public CommandValidationException(String message) {
    super(message);
  }
}
