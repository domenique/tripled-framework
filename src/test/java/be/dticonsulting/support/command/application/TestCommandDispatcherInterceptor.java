package be.dticonsulting.support.command.application;

public class TestCommandDispatcherInterceptor implements CommandDispatcherInterceptor {

  public boolean isInterceptorCalled = false;

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Command<ReturnType> command) throws Throwable {
    isInterceptorCalled = true;
    return chain.proceed();
  }
}
