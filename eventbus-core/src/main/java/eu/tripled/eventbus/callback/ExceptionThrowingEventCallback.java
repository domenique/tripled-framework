package eu.tripled.eventbus.callback;

import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.EventCallback;

public class ExceptionThrowingEventCallback<ReturnType> implements EventCallback<ReturnType> {

  private ReturnType result;

  @Override
  public void onSuccess(ReturnType result) {
    this.result = result;
  }

  @Override
  public void onValidationFailure(Event event) {
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
