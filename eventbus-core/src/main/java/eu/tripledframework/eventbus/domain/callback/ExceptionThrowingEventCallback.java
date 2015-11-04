package eu.tripledframework.eventbus.domain.callback;

import eu.tripledframework.eventbus.domain.EventCallback;

public class ExceptionThrowingEventCallback<ReturnType> implements EventCallback<ReturnType> {

  private ReturnType result;

  @Override
  public void onSuccess(ReturnType result) {
    this.result = result;
  }

  @Override
  public void onFailure(RuntimeException exception) {
    throw exception;
  }

  public ReturnType getResult() {
    return result;
  }
}
