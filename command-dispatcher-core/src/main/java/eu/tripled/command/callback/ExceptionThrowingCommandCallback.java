package eu.tripled.command.callback;

import eu.tripled.command.Command;
import eu.tripled.command.CommandCallback;

public class ExceptionThrowingCommandCallback<ReturnType> implements CommandCallback<ReturnType> {

  private ReturnType result;

  @Override
  public void onSuccess(ReturnType result) {
    this.result = result;
  }

  @Override
  public void onValidationFailure(Command<ReturnType> command) {
    throw new CommandValidationException("The command failed the validation step.");
  }

  @Override
  public void onFailure(Throwable exception) {
    throw new CommandFailedException(exception);
  }

  public ReturnType getResult() {
    return result;
  }
}
