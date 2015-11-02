package eu.tripledframework.eventbus.domain.synchronous;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.EventPublisher;
import eu.tripledframework.eventbus.domain.EventSubscriber;
import eu.tripledframework.eventbus.domain.annotation.Handles;
import eu.tripledframework.eventbus.domain.callback.AggregateEventCallback;
import eu.tripledframework.eventbus.domain.callback.ExceptionThrowingEventCallback;
import eu.tripledframework.eventbus.domain.dispatcher.EventDispatcher;
import eu.tripledframework.eventbus.domain.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerRepository;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
public class SynchronousEventBus implements EventPublisher, EventSubscriber {

  private final Logger logger = LoggerFactory.getLogger(SynchronousEventBus.class);
  private static final EventCallback<?> DEFAULT_CALLBACK = new ExceptionThrowingEventCallback<>();

  private final EventHandlerInvokerRepository invokerRepository;
  private final InterceptorChainFactory interceptorChainFactory;

  // constructors

  public SynchronousEventBus() {
    this.invokerRepository = new EventHandlerInvokerRepository();
    this.interceptorChainFactory = new InterceptorChainFactory();
  }

  public SynchronousEventBus(List<EventBusInterceptor> interceptors) {
    this.invokerRepository = new EventHandlerInvokerRepository();
    this.interceptorChainFactory = new InterceptorChainFactory(interceptors);
  }

  // subscribe methods

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
    getLogger().info("Subscribing {}.{}() to receive events of type {}", eventHandler.getClass().getSimpleName(), method.getName(), eventType.getName());
    invokerRepository.addEventHandlerInvoker(invoker);
  }

  // publish methods

  @Override
  public <ReturnType> Future<ReturnType>  publish(Object message) {
    return publish(message, (EventCallback<ReturnType>) DEFAULT_CALLBACK);
  }

  @Override
  public <ReturnType> Future<ReturnType> publish(Object event, EventCallback<ReturnType> callback) {
    Objects.requireNonNull(event, "The event cannot be null.");
    Objects.requireNonNull(callback, "The callback cannot be null.");
    getLogger().debug("Received an event for publication: {}", event);

    EventCallback<ReturnType> determinedCallback = determineCallbacks(callback);
    Future<ReturnType> future = getFutureCallback(callback);
    publishInternal(event, determinedCallback);

    getLogger().debug("Dispatched event {}", event);

    return future;
  }

    private <ReturnType> Future<ReturnType> getFutureCallback(EventCallback<ReturnType> callback) {
        return isDefaultCallback(callback) ? (Future<ReturnType>) callback : (Future<ReturnType>) DEFAULT_CALLBACK;
    }

    private <ReturnType> EventCallback<ReturnType> determineCallbacks(EventCallback<ReturnType> callback) {
    if (isDefaultCallback(callback)){
      return callback;
    } else {
      return new AggregateEventCallback(callback, DEFAULT_CALLBACK);
    }
  }

    private <ReturnType> boolean isDefaultCallback(EventCallback<ReturnType> callback) {
        return DEFAULT_CALLBACK.getClass() == callback.getClass();
    }

    protected <ReturnType> void publishInternal(Object event, EventCallback<ReturnType> callback) {
    new EventDispatcher<>(event, callback, invokerRepository, interceptorChainFactory)
        .dispatch();
  }


  protected Logger getLogger() {
    return logger;
  }

}
