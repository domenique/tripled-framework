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
import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.EventSubscriber;
import eu.tripledframework.eventbus.internal.infrastructure.callback.FutureCommandCallback;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.FunctionInvoker;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.UnitOfWorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

public class SynchronousEventBus implements CommandDispatcher, EventPublisher, EventSubscriber {

  private final Logger logger = LoggerFactory.getLogger(SynchronousEventBus.class);

  private final InvokerRepository invokerRepository;
  private final InterceptorChainFactory invokerInterceptorChainFactory;
  private final InterceptorChainFactory receiverInterceptorChainFactory;
  private final UnitOfWorkFactory unitOfWorkFactory;
  private final List<InvokerFactory> eventHandlerInvokerFactories;

  SynchronousEventBus(DefaultEventBusBuilder builder) {
    invokerRepository = builder.invokerRepository;
    invokerInterceptorChainFactory = builder.invokerInterceptorChainFactory;
    receiverInterceptorChainFactory = builder.receiverInterceptorChainFactory;
    unitOfWorkFactory = builder.unitOfWorkFactory;
    eventHandlerInvokerFactories = builder.eventHandlerInvokerFactories;
  }

  @Override
  public void subscribe(Object eventHandler) {
    eventHandlerInvokerFactories.stream()
            .filter(cur -> cur.supports(eventHandler))
            .findFirst()
            .ifPresent(f -> f.create(eventHandler)
                    .forEach(this::subscribeInternal));
  }

  protected void subscribeInternal(Invoker eventHandler) {
    getLogger().info("Adding subscription\t --> {}", eventHandler.toString());
    invokerRepository.add(eventHandler);
  }

  @Override
  public <ReturnType> Future<ReturnType> dispatch(Object command) {
    var future = new FutureCommandCallback<ReturnType>();
    dispatch(command, future);

    return future;
  }

  @Override
  public <ReturnType> void dispatch(Object command, CommandCallback<ReturnType> callback) {
    Objects.requireNonNull(command, "The command cannot be null.");
    Objects.requireNonNull(callback, "The callback cannot be null.");
    getLogger().debug("Received a command for publication: {}", command);

    var unitOfWork = unitOfWorkFactory.create();
    createInterceptorChainAroundDispatchInternal(command, callback, unitOfWork).proceed();

    getLogger().debug("Dispatched command {}", command);
  }

  private <ReturnType> InterceptorChain<ReturnType> createInterceptorChainAroundDispatchInternal(Object command, CommandCallback<ReturnType> callback,
                                                                                                 UnitOfWork unitOfWork) {
    var invoker = new FunctionInvoker(i -> {
      dispatchInternal(command, callback, unitOfWork);
      return null;
    });
    InterceptorChain<ReturnType> chain = receiverInterceptorChainFactory.createChain(command, unitOfWork, invoker);
    return chain;
  }

  protected <ReturnType> void dispatchInternal(Object event, CommandCallback<ReturnType> callback, UnitOfWork unitOfWork) {
    var invoker = invokerRepository.getByEventType(event.getClass());
    InterceptorChain<ReturnType> interceptorChain = invokerInterceptorChainFactory.createChain(event, unitOfWork, invoker);

    ReturnType response = null;
    RuntimeException thrownException = null;
    try {
      UnitOfWorkRepository.store(unitOfWork);
      response = interceptorChain.proceed();
      UnitOfWorkRepository.get().commit(this);
    } catch (RuntimeException exception) {
      UnitOfWorkRepository.get().rollback();
      thrownException = exception;
    }

    invokeAppropriateCallbackMethod(callback, response, thrownException);
    UnitOfWorkRepository.clear();
  }


  private <ReturnType> void invokeAppropriateCallbackMethod(CommandCallback<ReturnType> eventCallback,
                                                            ReturnType response, RuntimeException thrownException) {
    if (thrownException != null) {
      eventCallback.onFailure(thrownException);
    } else {
      eventCallback.onSuccess(response);
    }
  }

  @Override
  public void publish(Object event) {
    Objects.requireNonNull(event, "The event should not be null.");
    getLogger().debug("Received an event to publish. {}", event);

    if (UnitOfWorkRepository.isRunning()) {
      UnitOfWorkRepository.get().scheduleEvent(event);
      getLogger().debug("Scheduled event to be published later because a UnitOfWork exists for this thread.");
    } else {
      createInterceptorChainAroundPublishInternal(event).proceed();

      getLogger().debug("Published event {}", event);
    }

  }

  private InterceptorChain createInterceptorChainAroundPublishInternal(Object event) {
    var invoker = new FunctionInvoker(i -> {
      publishInternal(event, UnitOfWorkRepository.get());
      return null;
    });
    InterceptorChain chain = receiverInterceptorChainFactory.createChain(event, UnitOfWorkRepository.get(), invoker);
    return chain;
  }

  protected void publishInternal(Object event, UnitOfWork unitOfWork) {
    var invokers = invokerRepository.findAllByEventType(event.getClass());
    if (invokers.isEmpty()) {
      getLogger().warn("An event was published for which no EventHandler exists! {}", event);
    } else {
      InterceptorChain<?> interceptorChain = invokerInterceptorChainFactory.createChain(event, unitOfWork, invokers);
      interceptorChain.proceed();
    }
  }

  protected Logger getLogger() {
    return logger;
  }

}

