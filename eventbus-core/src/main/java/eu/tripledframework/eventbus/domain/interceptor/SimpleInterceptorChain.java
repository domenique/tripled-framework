package eu.tripledframework.eventbus.domain.interceptor;

import java.util.Iterator;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Object event;
  private final Iterator<EventBusInterceptor> interceptors;
  private final Iterator<EventHandlerInvoker> invokers;

  public SimpleInterceptorChain(Object event, Iterator<EventHandlerInvoker> invokers,
                                Iterator<EventBusInterceptor> interceptors) {
    this.event = event;
    this.invokers = invokers;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() {
    if (interceptors.hasNext()) {
      EventBusInterceptor nextInterceptor = interceptors.next();
      return nextInterceptor.intercept(this, event);
    } else {
      return invokeEventHandlers();
    }
  }

  @SuppressWarnings("unchecked")
  private ReturnType invokeEventHandlers() {
    ReturnType response = null;
    while (invokers.hasNext()) {
      EventHandlerInvoker current = invokers.next();
      if (current.hasReturnType()) {
        response = (ReturnType) current.invoke(event);
      } else {
        current.invoke(event);
      }
    }

    return response;
  }
}
