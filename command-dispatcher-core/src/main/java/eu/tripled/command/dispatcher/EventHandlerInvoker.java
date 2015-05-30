package eu.tripled.command.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandlerInvoker {

  private Object eventHandler;
  private Method method;

  public EventHandlerInvoker(Object eventHandler, Method method) {
    this.eventHandler = eventHandler;
    this.method = method;
  }

  public Object invoke(Object object) {
    try {
      return method.invoke(eventHandler, object);
    } catch (InvocationTargetException | IllegalAccessException e) {
      String errorMsg = String.format("Could not invoke EventHandler method %s on %s", method.getName(), eventHandler.getClass().getSimpleName());
      throw new EventHandlerInvocationException(errorMsg, e);
    }
  }

}
