package eu.tripledframework.eventbus.interceptor;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.InterceptorChain;
import eu.tripledframework.eventbus.invoker.EventHandlerInvoker;

import java.util.List;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Object event;
  private final List<EventBusInterceptor> interceptors;
  private final EventHandlerInvoker invoker;

  public SimpleInterceptorChain(Object event, EventHandlerInvoker invoker, List<EventBusInterceptor> interceptors) {
    this.event = event;
    this.invoker = invoker;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() throws Exception {
    if (interceptors.size() > 0) {
      EventBusInterceptor nextInterceptor = interceptors.remove(0);
      return nextInterceptor.intercept(this, event);
    } else {
      // TODO: Catch exceptions ?
      return (ReturnType) invoker.invoke(event);
    }
  }
}
