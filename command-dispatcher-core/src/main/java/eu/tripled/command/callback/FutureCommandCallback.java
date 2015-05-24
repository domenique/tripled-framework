package eu.tripled.command.callback;

import eu.tripled.command.Command;
import eu.tripled.command.CommandCallback;

import java.util.concurrent.*;

public class FutureCommandCallback<ReturnType> implements CommandCallback<ReturnType>, Future<ReturnType> {

  private ReturnType result;
  private Throwable exception;
  private CountDownLatch countDownLatch = new CountDownLatch(1);

  @Override
  public void onSuccess(ReturnType result) {
    this.result = result;
    countDownLatch.countDown();
  }

  @Override
  public void onValidationFailure(Command<ReturnType> command) {
    this.exception = new CommandValidationException("The command failed the validation step.");
    countDownLatch.countDown();
  }

  @Override
  public void onFailure(Throwable exception) {
    this.exception = exception;
    countDownLatch.countDown();
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return countDownLatch.getCount() == 0;
  }

  @Override
  public ReturnType get() throws InterruptedException, ExecutionException {
    if (!isDone()) {
      countDownLatch.await();
    }

    return returnResultOrException();
  }

  @Override
  public ReturnType get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    if (!isDone() && !countDownLatch.await(timeout, unit)) {
      throw new TimeoutException("Timeout occurred when waiting for the task to complete.");
    }

    return returnResultOrException();
  }

  private ReturnType returnResultOrException() throws ExecutionException {
    if (exception != null) {
      throw new ExecutionException(exception);
    }
    return result;
  }
}
