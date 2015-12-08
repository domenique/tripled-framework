/*
 * Copyright 2015 TripleD, DTI-Consulting.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.tripledframework.eventbus.domain.callback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.tripledframework.eventbus.domain.EventCallback;

public class FutureEventCallback<ReturnType> implements EventCallback<ReturnType>, Future<ReturnType> {

  private ReturnType result;
  private Throwable exception;
  private CountDownLatch countDownLatch = new CountDownLatch(1);

  @Override
  public void onSuccess(ReturnType result) {
    this.result = result;
    countDownLatch.countDown();
  }

  @Override
  public void onFailure(RuntimeException exception) {
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
