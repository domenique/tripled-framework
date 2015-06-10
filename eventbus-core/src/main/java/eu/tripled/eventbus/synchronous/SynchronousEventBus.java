package eu.tripled.eventbus.synchronous;

import com.google.common.base.Preconditions;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
public class SynchronousEventBus implements EventPublisher, EventSubscriber {

  private static final Logger LOGGER = LoggerFactory.getLogger(SynchronousEventBus.class);

  private ConcurrentMap<Class<?>, EventHandlerInvoker> eventHandlers;
  private List<EventBusInterceptor> interceptors = new ArrayList<>();

  public SynchronousEventBus() {
    this.eventHandlers = new ConcurrentHashMap<>();
    this.interceptors = new ArrayList<>();
  }

  public SynchronousEventBus(List<EventBusInterceptor> interceptors) {
    this.eventHandlers = new ConcurrentHashMap<>();
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

  protected void subscribeInternal(Object eventHandler, Class<?> command, Method method) {
    if (eventHandlers.containsKey(command)) {
      eventHandlers.get(command).addObjectMethodPair(eventHandler, method);
    } else {
      eventHandlers.put(command, new EventHandlerInvoker(eventHandler, method));
    }
  }

  protected <ReturnType> void dispatchInternal(Event event, EventCallback<ReturnType> callback) {
    EventHandlerInvoker invoker = getInvoker(event);
    InterceptorChain<ReturnType> chain = createChain(event, invoker);

    try {
      ReturnType response = chain.proceed();
      callback.onSuccess(response);
    } catch (CommandValidationException validationEx) {
      callback.onValidationFailure(event);
    } catch (Throwable exception) {
      callback.onFailure(exception);
    }
  }

  private EventHandlerInvoker getInvoker(Event event) {
    EventHandlerInvoker invoker = eventHandlers.get(event.getBody().getClass());
    if (invoker == null) {
      throw new EventHandlerNotFoundException(String.format("Could not find a command handler for %s", event.getBody().getClass().getSimpleName()));
    }
    return invoker;
  }


  protected <ReturnType> SimpleInterceptorChain<ReturnType> createChain(Event event, EventHandlerInvoker invoker) {
    return new SimpleInterceptorChain<>(event, invoker, Lists.newArrayList(interceptors));
  }
}
