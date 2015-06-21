package eu.tripledframework.eventbus.domain.interceptor;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;

import java.util.Iterator;

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
  public ReturnType proceed() throws Exception {
    if (interceptors.hasNext()) {
      EventBusInterceptor nextInterceptor = interceptors.next();
      return nextInterceptor.intercept(this, event);
    } else {
      // TODO: Catch exceptions ?
      return (ReturnType) invoker.invoke(event);
    }
  }
}
