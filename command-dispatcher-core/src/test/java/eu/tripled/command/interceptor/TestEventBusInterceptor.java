package eu.tripled.command.interceptor;

import eu.tripled.command.Command;
import eu.tripled.command.EventBusInterceptor;
import eu.tripled.command.InterceptorChain;

public class TestEventBusInterceptor implements EventBusInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command command) throws Throwable {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
