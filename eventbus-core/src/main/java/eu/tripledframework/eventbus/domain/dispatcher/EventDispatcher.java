package eu.tripledframework.eventbus.domain.dispatcher;

import java.util.Iterator;
import java.util.List;

import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerRepository;

/**
 * The event dispatcher is responsible for dispatching an event to all registered handlers.
 * <p>
 * There can be at most one event handler with a returnType specified for any given event type.
 * If an event handler exists with a returnType, this one is executed first, the order of other event handlers is undefined and should not be
 * relied upon. If the any of the event handlers fail, the others are still invoked.
 *
 * @param <ReturnType> The return type of the commandHandler it dispatches to.
 */
public class EventDispatcher<ReturnType> {

  private final EventHandlerInvokerRepository invokerRepository;
  private final InterceptorChainFactory interceptorChainFactory;
  private final Object event;
  private final EventCallback<ReturnType> callback;

  public EventDispatcher(Object event, EventCallback<ReturnType> callback,
                         EventHandlerInvokerRepository invokerRepository, InterceptorChainFactory interceptorChainFactory) {
    this.event = event;
    this.callback = callback;
    this.invokerRepository = invokerRepository;
    this.interceptorChainFactory = interceptorChainFactory;
  }

  public void dispatch() {
    List<EventHandlerInvoker> invokers = invokerRepository.findAllByEventType(event.getClass());
    assertInvokerIsFound(invokers);

    ReturnType response = null;
    RuntimeException thrownException = null;
      try {
        response = executeChain(event, invokers.iterator());
      } catch (RuntimeException exception) {
        thrownException = exception;
      }
    invokeAppropriateCallbackMethod(response, thrownException);
  }

  private ReturnType executeChain(Object event, Iterator<EventHandlerInvoker> eventHandlerInvoker) {
    InterceptorChain<ReturnType> chain = interceptorChainFactory.createChain(event, eventHandlerInvoker);
    return chain.proceed();
  }

  private void invokeAppropriateCallbackMethod(ReturnType response, RuntimeException thrownException) {
    if (thrownException != null) {
      callback.onFailure(thrownException);
    } else {
      callback.onSuccess(response);
    }
  }

  private void assertInvokerIsFound(List<EventHandlerInvoker> invokersWithReturnType) {
    if (invokersWithReturnType == null || invokersWithReturnType.isEmpty()) {
      throw new EventHandlerNotFoundException(String.format("Could not find an event handler for %s", event));
    }
  }
}
