package eu.tripled.eventbus.synchronous;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import eu.tripled.eventbus.*;
import eu.tripled.eventbus.annotation.Handles;
import eu.tripled.eventbus.callback.CommandValidationException;
import eu.tripled.eventbus.callback.ExceptionThrowingEventCallback;
import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.interceptor.SimpleInterceptorChain;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
public class SynchronousEventBus implements EventPublisher, EventSubscriber {

  private static final Logger LOGGER = LoggerFactory.getLogger(SynchronousEventBus.class);

  private Set<EventHandlerInvoker> eventHandlers;
  private List<EventBusInterceptor> interceptors = new ArrayList<>();

  public SynchronousEventBus() {
    this.eventHandlers = new CopyOnWriteArraySet<>();
    this.interceptors = new ArrayList<>();
  }

  public SynchronousEventBus(List<EventBusInterceptor> interceptors) {
    this.eventHandlers = new CopyOnWriteArraySet<>();
    this.interceptors = Collections.unmodifiableList(interceptors);
  }

  @Override
  public void subscribe(Object eventHandler) {
    Set<Method> methods = ReflectionUtils.getAllMethods(eventHandler.getClass(),
        ReflectionUtils.withAnnotation(Handles.class));

    for (Method method : methods) {
      Handles annotation = method.getAnnotation(Handles.class);
      subscribeInternal(eventHandler, annotation.value(), method);
    }
  }

  protected void subscribeInternal(Object eventHandler, Class<?> eventType, Method method) {
    EventHandlerInvoker invoker = new EventHandlerInvoker(eventType, eventHandler, method);

    if (!eventHandlers.contains(invoker)) {
      if (invoker.hasReturnType() && !invokerWithReturnType(eventType).isPresent() || !invoker.hasReturnType()) {
        eventHandlers.add(invoker);
      } else if (invoker.hasReturnType() && invokerWithReturnType(eventType).isPresent()) {
        throw new DuplicateEventHandlerRegistrationException(String.format("An eventHandler with return type for event %s already exists.", eventType));
      }
    }

  }

  @Override
  public <ReturnType> void publish(Object message, EventCallback<ReturnType> callback) {
    Preconditions.checkArgument(message != null, "The message cannot be null.");
    Preconditions.checkArgument(callback != null, "The callback cannot be null.");
    LOGGER.debug("Received a message to publish: {}", message.getClass().getSimpleName());

    dispatchInternal(new Event<>(message), callback);

    LOGGER.debug("Finished executing commandMessage {}", message.getClass().getSimpleName());
  }

  @Override
  public void publish(Object message) {
    publish(message, new ExceptionThrowingEventCallback<>());
  }

  protected <ReturnType> void dispatchInternal(Event event, EventCallback<ReturnType> callback) {
    ReturnType response = null;
    boolean failureOccurred = false;
    Optional<EventHandlerInvoker> optionalEventHandlerInvokerWithReturnType = invokerWithReturnType(event.getBody().getClass());
    List<EventHandlerInvoker> eventHandlersWithReturnType = allInvokersWithoutReturnTypeFor(event.getBody().getClass());

    if (!optionalEventHandlerInvokerWithReturnType.isPresent() && eventHandlersWithReturnType.isEmpty()) {
      throw new EventHandlerNotFoundException(String.format("Could not find an event handler for %s", event));
    }

    // first invoke a handler with return type.
    if (optionalEventHandlerInvokerWithReturnType.isPresent()) {
      InterceptorChain<ReturnType> chainForEventHandlerWithReturnType = createChain(event, optionalEventHandlerInvokerWithReturnType.get());
      try {
        response = chainForEventHandlerWithReturnType.proceed();
      } catch (CommandValidationException validationEx) {
        callback.onValidationFailure(event);
        failureOccurred = true;
      } catch (Throwable exception) {
        callback.onFailure(exception);
        failureOccurred = true;
      }

    }

    // now all the event handlers without a return type.
    for (EventHandlerInvoker eventHandlerInvoker : eventHandlersWithReturnType) {
      InterceptorChain<ReturnType> chain = createChain(event, eventHandlerInvoker);

      try {
        chain.proceed();
      } catch (CommandValidationException validationEx) {
        callback.onValidationFailure(event);
        failureOccurred = true;
      } catch (Throwable exception) {
        callback.onFailure(exception);
        failureOccurred = true;
      }
    }

    // invoke the callback with the response object.
    if (!failureOccurred) {
      callback.onSuccess(response);
    }
  }

  protected <ReturnType> SimpleInterceptorChain<ReturnType> createChain(Event event, EventHandlerInvoker invoker) {
    return new SimpleInterceptorChain<>(event, invoker, Lists.newArrayList(interceptors));
  }


  private List<EventHandlerInvoker> allInvokersWithoutReturnTypeFor(Class<?> eventType) {
    return Lists.newArrayList(Iterables.filter(eventHandlers, input -> (input.handles(eventType) && !input.hasReturnType())));
  }

  private Optional<EventHandlerInvoker> invokerWithReturnType(Class<?> eventType) {
    return Iterables.tryFind(eventHandlers, input -> input.handles(eventType) && input.hasReturnType());
  }
}
