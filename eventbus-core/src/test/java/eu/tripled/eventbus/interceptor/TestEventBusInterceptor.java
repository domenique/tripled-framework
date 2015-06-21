package eu.tripled.eventbus.interceptor;

import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.InterceptorChain;

public class TestEventBusInterceptor implements EventBusInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) throws Exception {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
