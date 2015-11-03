package eu.tripledframework.eventbus.domain.interceptor;

import java.util.Iterator;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Object event;
  private final Iterator<EventBusInterceptor> interceptors;
  private final EventHandlerInvoker invoker;

  public SimpleInterceptorChain(Object event, EventHandlerInvoker invoker, Iterator<EventBusInterceptor> interceptors) {
    this.event = event;
    this.invoker = invoker;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() {
    if (interceptors.hasNext()) {
      EventBusInterceptor nextInterceptor = interceptors.next();
      return nextInterceptor.intercept(this, event);
    } else {
      return (ReturnType) invoker.invoke(event);
    }
  }
}
