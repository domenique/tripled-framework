package eu.tripledframework.eventbus.synchronous;

import com.google.common.base.Preconditions;
import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.EventCallback;
import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.EventSubscriber;
import eu.tripledframework.eventbus.annotation.Handles;
import eu.tripledframework.eventbus.callback.ExceptionThrowingEventCallback;
import eu.tripledframework.eventbus.dispatcher.EventDispatcher;
import eu.tripledframework.eventbus.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.invoker.EventHandlerInvokerRepository;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
public class SynchronousEventBus implements EventPublisher, EventSubscriber {

  private final Logger logger = LoggerFactory.getLogger(SynchronousEventBus.class);

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
    invokerRepository.addEventHandlerInvoker(invoker);
  }

  // publish methods

  @Override
  public void publish(Object message) {
    publish(message, new ExceptionThrowingEventCallback<>());
  }

  @Override
  public <ReturnType> void publish(Object event, Future<ReturnType> callback) {
    Preconditions.checkArgument(callback instanceof EventCallback, "The callback should be an instance of EventCallBack.");
    publish(event, (EventCallback<ReturnType>) callback);
  }

  @Override
  public <ReturnType> void publish(Object event, EventCallback<ReturnType> callback) {
    Preconditions.checkArgument(event != null, "The event cannot be null.");
    Preconditions.checkArgument(callback != null, "The callback cannot be null.");
    getLogger().debug("Received an event for publication: {}", event);

    publishInternal(event, callback);

    getLogger().debug("Dispatched event {}", event);
  }

  protected <ReturnType> void publishInternal(Object event, EventCallback<ReturnType> callback) {
    new EventDispatcher<>(event, callback, invokerRepository, interceptorChainFactory)
        .dispatch();
  }


  protected Logger getLogger() {
    return logger;
  }

}
