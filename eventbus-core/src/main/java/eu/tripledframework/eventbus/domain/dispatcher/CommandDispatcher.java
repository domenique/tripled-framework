package eu.tripledframework.eventbus.domain.dispatcher;

import java.util.Iterator;
import java.util.List;

import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvokerRepository;

public class CommandDispatcher<ReturnType> implements Dispatcher {

  private final EventHandlerInvokerRepository invokerRepository;
  private final InterceptorChainFactory interceptorChainFactory;
  private final Object event;
  private final EventCallback<ReturnType> callback;

  public CommandDispatcher(Object event, EventCallback<ReturnType> callback,
                           EventHandlerInvokerRepository invokerRepository, InterceptorChainFactory interceptorChainFactory) {
    this.event = event;
    this.callback = callback;
    this.invokerRepository = invokerRepository;
    this.interceptorChainFactory = interceptorChainFactory;
  }

  @Override
  public void dispatch() {
    // TODO: We should have only one handler for a command. so we should enforce this.
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
