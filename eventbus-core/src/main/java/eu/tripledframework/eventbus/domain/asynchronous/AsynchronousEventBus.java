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

package eu.tripledframework.eventbus.domain.asynchronous;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.synchronous.SynchronousEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An Implementation of the EventDispatcher which executes the command in an asynchronous fashion.
 * This implementation uses a ThreadPool to dispatch events to separate threads.
 */
public class AsynchronousEventBus extends SynchronousEventBus {

  private final Logger logger = LoggerFactory.getLogger(AsynchronousEventBus.class);

  private Executor executor;

  public AsynchronousEventBus() {
    super();
    this.executor = Executors.newCachedThreadPool();
  }

  public AsynchronousEventBus(Executor executor) {
    super();
    this.executor = executor;
  }

  public AsynchronousEventBus(List<EventBusInterceptor> interceptors, Executor executor) {
    super(interceptors);
    this.executor = executor;
  }

  @Override
  protected <ReturnType> void dispatchInternal(Object message, EventCallback<ReturnType> callback) {
    executor.execute(new RunnableCommand<>(message, callback));
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }

  private class RunnableCommand<ReturnType> implements Runnable {

    private final Object message;
    private final EventCallback<ReturnType> callback;

    public RunnableCommand(Object message, EventCallback<ReturnType> callback) {
      this.message = message;
      this.callback = callback;
    }

    @Override
    public void run() {
      AsynchronousEventBus.super.dispatchInternal(message, callback);
    }
  }
}
