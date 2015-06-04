package eu.tripled.eventbus.interceptor;

import eu.tripled.eventbus.event.Event;
import eu.tripled.eventbus.EventBusInterceptor;
import eu.tripled.eventbus.InterceptorChain;

public class TestEventBusInterceptor implements EventBusInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Event event) throws Throwable {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
