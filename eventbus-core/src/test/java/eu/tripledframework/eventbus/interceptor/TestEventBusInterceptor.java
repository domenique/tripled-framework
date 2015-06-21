package eu.tripledframework.eventbus.interceptor;

import eu.tripledframework.eventbus.EventBusInterceptor;
import eu.tripledframework.eventbus.InterceptorChain;

public class TestEventBusInterceptor implements EventBusInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) throws Exception {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
