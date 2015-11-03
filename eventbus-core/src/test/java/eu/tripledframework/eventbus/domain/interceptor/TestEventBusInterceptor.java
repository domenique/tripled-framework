package eu.tripledframework.eventbus.domain.interceptor;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;

public class TestEventBusInterceptor implements EventBusInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
