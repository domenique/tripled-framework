package eu.tripled.eventbus.interceptor;

import com.google.common.collect.Lists;
import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.invoker.EventHandlerInvoker;

import java.util.Collections;
import java.util.List;

public class InterceptorChainFactory {

  private final List<EventBusInterceptor> interceptors;

  public InterceptorChainFactory() {
    this.interceptors = Collections.unmodifiableList(Collections.emptyList());
  }

  public InterceptorChainFactory(List<EventBusInterceptor> interceptors) {
    this.interceptors = Collections.unmodifiableList(interceptors);
  }

  public <ReturnType> SimpleInterceptorChain<ReturnType> createChain(Event event, EventHandlerInvoker invoker) {
    return new SimpleInterceptorChain<>(event, invoker, Lists.newArrayList(interceptors));
  }

}
