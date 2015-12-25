package eu.tripledframework.eventbus.domain.dispatcher;

import java.util.Optional;

import eu.tripledframework.eventbus.domain.EventCallback;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.interceptor.InterceptorChainFactory;
import eu.tripledframework.eventbus.domain.invoker.Invoker;
import eu.tripledframework.eventbus.domain.invoker.InvokerRepository;

public class CommandDispatcher<ReturnType> implements Dispatcher {

  private final InvokerRepository invokerRepository;
  private final InterceptorChainFactory interceptorChainFactory;
  private final Object event;
  private final EventCallback<ReturnType> callback;

  public CommandDispatcher(Object event, EventCallback<ReturnType> callback,
                           InvokerRepository invokerRepository, InterceptorChainFactory interceptorChainFactory) {
    this.event = event;
    this.callback = callback;
    this.invokerRepository = invokerRepository;
    this.interceptorChainFactory = interceptorChainFactory;
  }

  @Override
  public void dispatch() {
    Optional<Invoker> invoker = invokerRepository.findByEventType(event.getClass());
    assertInvokerIsFound(invoker);

    ReturnType response = null;
    RuntimeException thrownException = null;
      try {
        response = executeChain(event, invoker.get());
      } catch (RuntimeException exception) {
        thrownException = exception;
      }
    invokeAppropriateCallbackMethod(response, thrownException);
  }

  private ReturnType executeChain(Object event, Invoker invoker) {
    InterceptorChain<ReturnType> chain = interceptorChainFactory.createChain(event, invoker);
    return chain.proceed();
  }

  private void invokeAppropriateCallbackMethod(ReturnType response, RuntimeException thrownException) {
    if (thrownException != null) {
      callback.onFailure(thrownException);
    } else {
      callback.onSuccess(response);
    }
  }

  private void assertInvokerIsFound(Optional<Invoker> invoker) {
    if (!invoker.isPresent()) {
      throw new HandlerNotFoundException(String.format("Could not find an event handler for %s", event));
    }
  }
}
