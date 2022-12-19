/*
 * Copyright 2022 TripleD framework.
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
package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.CommandCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * An Implementation of the CommandDispatcher which executes the command and events in an asynchronous fashion.
 * This implementation uses a ThreadPool to dispatch command and events to separate threads.
 */
public class AsynchronousEventBus extends SynchronousEventBus {

  private final Logger logger = LoggerFactory.getLogger(AsynchronousEventBus.class);

  private Executor executor;

  AsynchronousEventBus(DefaultEventBusBuilder builder) {
    super(builder);
    executor = builder.executor;
  }

  @Override
  protected <ReturnType> void dispatchInternal(Object message, CommandCallback<ReturnType> callback, UnitOfWork unitOfWork) {
    executor.execute(new RunnableDispatch<>(message, callback, unitOfWork));
  }

  @Override
  protected void publishInternal(Object event, UnitOfWork unitOfWork) {
    executor.execute(new RunnablePublish(event, unitOfWork));
  }

  private class RunnableDispatch<ReturnType> implements Runnable {

    private final Object message;
    private final CommandCallback<ReturnType> callback;
    private final UnitOfWork unitOfWork;

    public RunnableDispatch(Object message, CommandCallback<ReturnType> callback, UnitOfWork unitOfWork) {
      this.message = message;
      this.callback = callback;
      this.unitOfWork = unitOfWork;
    }

    @Override
    public void run() {
      AsynchronousEventBus.super.dispatchInternal(message, callback, unitOfWork);
    }
  }

  private class RunnablePublish implements Runnable {

    private UnitOfWork unitOfWork;
    private Object event;

    private RunnablePublish(Object event, UnitOfWork unitOfWork) {
      this.unitOfWork = unitOfWork;
      this.event = event;
    }

    @Override
    public void run() {
      AsynchronousEventBus.super.publishInternal(event, unitOfWork);
    }
  }


  @Override
  protected Logger getLogger() {
    return logger;
  }
}
