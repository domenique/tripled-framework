package eu.tripledframework.eventbus.domain.dispatcher;

import java.util.List;
import java.util.Optional;

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
 * @param <ReturnType>
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
    Optional<EventHandlerInvoker> optionalInvokerWithReturnType = invokerRepository.findByEventWithReturnType(event.getClass());
    List<EventHandlerInvoker> invokersWithoutReturnType = invokerRepository.findAllByEventWithoutReturnType(event.getClass());

    assertInvokerIsFound(optionalInvokerWithReturnType, invokersWithoutReturnType);

    ReturnType response = null;
    Exception thrownException = null;
    // first dispatch a handler with return type.
    if (optionalInvokerWithReturnType.isPresent()) {
      try {
        response = executeChain(event, optionalInvokerWithReturnType.get());
      } catch (Exception exception) {
        thrownException = exception;
      }
    }

    // now all the event handlers without a return type.
    for (EventHandlerInvoker eventHandlerInvoker : invokersWithoutReturnType) {
      try {
        executeChain(event, eventHandlerInvoker);
      } catch (Exception exception) {
        thrownException = exception;
      }
    }

    invokeAppropriateCallback(response, thrownException);
  }

  private <ReturnType> ReturnType executeChain(Object event, EventHandlerInvoker eventHandlerInvoker) throws Exception {
    InterceptorChain<ReturnType> chain = interceptorChainFactory.createChain(event, eventHandlerInvoker);
    return chain.proceed();
  }

  private void invokeAppropriateCallback(ReturnType response, Exception thrownException) {
    if (thrownException != null) {
      callback.onFailure(thrownException);
    } else {
      callback.onSuccess(response);
    }
  }

  private void assertInvokerIsFound(Optional<EventHandlerInvoker> optionalInvokerWithReturnType, List<EventHandlerInvoker> invokersWithReturnType) {
    if (!optionalInvokerWithReturnType.isPresent() && invokersWithReturnType.isEmpty()) {
      throw new EventHandlerNotFoundException(String.format("Could not find an event handler for %s", event));
    }
  }
}
