package be.dticonsulting.command.interceptor;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.CommandDispatcherInterceptor;
import be.dticonsulting.command.InterceptorChain;

public class TestCommandDispatcherInterceptor implements CommandDispatcherInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command<ReturnType> command) throws Throwable {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
