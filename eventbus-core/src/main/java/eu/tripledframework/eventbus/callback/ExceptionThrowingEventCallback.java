package eu.tripledframework.eventbus.callback;

import eu.tripledframework.eventbus.EventCallback;

public class ExceptionThrowingEventCallback<ReturnType> implements EventCallback<ReturnType> {

  private ReturnType result;

  @Override
  public void onSuccess(ReturnType result) {
    this.result = result;
  }

  @Override
  public void onFailure(Throwable exception) {
    throw new CommandFailedException(exception);
  }

  public ReturnType getResult() {
    return result;
  }
}
