package be.dticonsulting.command.interceptor;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.CommandDispatcherInterceptor;
import be.dticonsulting.command.InterceptorChain;

import java.util.List;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Command<ReturnType> command;
  private final List<CommandDispatcherInterceptor> interceptors;

  public SimpleInterceptorChain(Command<ReturnType> command, List<CommandDispatcherInterceptor> interceptors) {
    this.command = command;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() throws Throwable {
    if (interceptors.size() > 0) {
      CommandDispatcherInterceptor nextInterceptor = interceptors.remove(0);
      return nextInterceptor.intercept(this, command);
    } else {
      return command.execute();
    }
  }
}
