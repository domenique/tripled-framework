package eu.tripledframework.eventbus.domain.callback;

public class ExceptionThrowingEventCallback<ReturnType> extends FutureEventCallback<ReturnType> {

  @Override
  public void onFailure(Throwable exception) {
    if (exception instanceof RuntimeException) {
      throw (RuntimeException) exception;
    } else {
      throw new CommandFailedException(exception);
    }
  }

  public ReturnType getResult() {
    return getResult();
  }
}
