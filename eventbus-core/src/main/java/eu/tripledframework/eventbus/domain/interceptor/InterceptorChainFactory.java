package eu.tripledframework.eventbus.domain.interceptor;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;

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

  public <ReturnType> SimpleInterceptorChain<ReturnType> createChain(Object event, EventHandlerInvoker invoker) {
    return new SimpleInterceptorChain<>(event, invoker, interceptors.iterator());
  }

}
