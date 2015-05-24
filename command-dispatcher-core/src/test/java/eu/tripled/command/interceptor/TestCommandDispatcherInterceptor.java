package eu.tripled.command.interceptor;

import eu.tripled.command.Command;
import eu.tripled.command.CommandDispatcherInterceptor;
import eu.tripled.command.InterceptorChain;

public class TestCommandDispatcherInterceptor implements CommandDispatcherInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command<ReturnType> command) throws Throwable {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
