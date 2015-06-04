package eu.tripled.eventbus.interceptor;

import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.InterceptorChain;
import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.synchronous.EventHandlerInvoker;

import java.util.List;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Event event;
  private final List<EventBusInterceptor> interceptors;
  private final EventHandlerInvoker invoker;

  public SimpleInterceptorChain(Event event, EventHandlerInvoker invoker, List<EventBusInterceptor> interceptors) {
    this.event = event;
    this.invoker = invoker;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() throws Throwable {
    if (interceptors.size() > 0) {
      EventBusInterceptor nextInterceptor = interceptors.remove(0);
      return nextInterceptor.intercept(this, event);
    } else {
      // TODO: Catch exceptions ?
      return (ReturnType) invoker.invoke(event.getBody());
    }
  }
}
