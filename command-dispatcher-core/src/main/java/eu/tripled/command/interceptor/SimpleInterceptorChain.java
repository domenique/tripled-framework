package eu.tripled.command.interceptor;

import eu.tripled.command.Command;
import eu.tripled.command.EventBusInterceptor;
import eu.tripled.command.InterceptorChain;
import eu.tripled.command.dispatcher.EventHandlerInvoker;

import java.util.List;

public class SimpleInterceptorChain<ReturnType> implements InterceptorChain<ReturnType> {

  private final Command command;
  private final List<EventBusInterceptor> interceptors;
  private final EventHandlerInvoker invoker;

  public SimpleInterceptorChain(Command command, EventHandlerInvoker invoker, List<EventBusInterceptor> interceptors) {
    this.command = command;
    this.invoker = invoker;
    this.interceptors = interceptors;
  }

  @Override
  public ReturnType proceed() throws Throwable {
    if (interceptors.size() > 0) {
      EventBusInterceptor nextInterceptor = interceptors.remove(0);
      return nextInterceptor.intercept(this, command);
    } else {
      // TODO: Catch exceptions ?
      return (ReturnType) invoker.invoke(command.getBody());
    }
  }
}
