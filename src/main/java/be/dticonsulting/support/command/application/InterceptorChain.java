package be.dticonsulting.support.command.application;

import java.util.List;

public class InterceptorChain<ReturnType> {

  private final Command<ReturnType> command;
  private final List<CommandDispatcherInterceptor> interceptors;

  public InterceptorChain(Command<ReturnType> command, List<CommandDispatcherInterceptor> interceptors) {
    this.command = command;
    this.interceptors = interceptors;

  }

  public ReturnType proceed() throws Throwable {
    if (interceptors.size() > 0) {
      CommandDispatcherInterceptor nextInterceptor = interceptors.remove(0);
      return nextInterceptor.intercept(this, command);
    } else {
      return command.execute();
    }
  }

}
