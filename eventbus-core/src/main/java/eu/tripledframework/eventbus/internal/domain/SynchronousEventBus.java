/*
 * Copyright 2016 TripleD framework.
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
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.UnitOfWorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

public class SynchronousEventBus implements CommandDispatcher, EventPublisher, EventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(SynchronousEventBus.class);

    private final InvokerRepository invokerRepository;
    private final InterceptorChainFactory interceptorChainFactory;
    private UnitOfWorkFactory unitOfWorkFactory;
    private List<InvokerFactory> eventHandlerInvokerFactories;

    public SynchronousEventBus(InvokerRepository invokerRepository, InterceptorChainFactory interceptorChainFactory,
                               List<InvokerFactory> invokerFactories, UnitOfWorkFactory unitOfWorkFactory) {
        this.invokerRepository = invokerRepository;
        this.interceptorChainFactory = interceptorChainFactory;
        this.eventHandlerInvokerFactories = invokerFactories;
        this.unitOfWorkFactory = unitOfWorkFactory;
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
        getLogger().info("Adding Event subscription for {}", eventHandler.toString());
        invokerRepository.add(eventHandler);
    }

    @Override
    public <ReturnType> Future<ReturnType> dispatch(Object command) {
        FutureCommandCallback<ReturnType> future = new FutureCommandCallback<>();
        dispatch(command, future);

        return future;
    }

    @Override
    public <ReturnType> void dispatch(Object command, CommandCallback<ReturnType> callback) {
        Objects.requireNonNull(command, "The command cannot be null.");
        Objects.requireNonNull(callback, "The callback cannot be null.");
        getLogger().debug("Received a command for publication: {}", command);

        UnitOfWork unitOfWork = unitOfWorkFactory.create();
        dispatchInternal(command, callback, unitOfWork);

        getLogger().debug("Dispatched command {}", command);
    }

    protected <ReturnType> void dispatchInternal(Object event, CommandCallback<ReturnType> callback, UnitOfWork unitOfWork) {
        Invoker invoker = invokerRepository.getByEventType(event.getClass());
        InterceptorChain<ReturnType> interceptorChain = interceptorChainFactory.createChain(event, unitOfWork, invoker);

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
            publishInternal(event, UnitOfWorkRepository.get());
            getLogger().debug("Published event {}", event);
        }

    }

    protected void publishInternal(Object event, UnitOfWork unitOfWork) {
        List<Invoker> invokers = invokerRepository.findAllByEventType(event.getClass());
        if (invokers.isEmpty()) {
            getLogger().warn("An event was published for which no EventHandler exists! {}", event);
        } else {
            InterceptorChain<?> interceptorChain = interceptorChainFactory.createChain(event, unitOfWork, invokers);
            interceptorChain.proceed();
        }
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}

