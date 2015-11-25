package eu.tripledframework.eventbus.domain.interceptor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import eu.tripledframework.eventbus.domain.invoker.EventHandlerInvoker;

public class InterceptorChainFactory {

  private final List<EventBusInterceptor> interceptors;

  public InterceptorChainFactory() {
    this.interceptors = Collections.unmodifiableList(Collections.emptyList());
  }

  public InterceptorChainFactory(List<EventBusInterceptor> interceptors) {
    this.interceptors = Collections.unmodifiableList(interceptors);
  }

  public <ReturnType> InterceptorChain<ReturnType> createChain(Object event, Iterator<EventHandlerInvoker> invoker) {
    return new SimpleInterceptorChain<>(event, invoker, interceptors.iterator());
  }

}
