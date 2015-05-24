package eu.tripled.command.interceptor;

import eu.tripled.command.Command;
import eu.tripled.command.CommandDispatcherInterceptor;
import eu.tripled.command.InterceptorChain;

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
