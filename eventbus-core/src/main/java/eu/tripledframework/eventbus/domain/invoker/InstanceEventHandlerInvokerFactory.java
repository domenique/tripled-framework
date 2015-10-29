package eu.tripledframework.eventbus.domain.invoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.ReflectionUtils;

import eu.tripledframework.eventbus.domain.annotation.Handles;

public class InstanceEventHandlerInvokerFactory implements EventHandlerInvokerFactory {

  @Override
  public List<EventHandlerInvoker> create(Object eventHandler) {
    List<EventHandlerInvoker> invokers = new ArrayList<>();

    Set<Method> methods = ReflectionUtils.getAllMethods(eventHandler.getClass(),
        ReflectionUtils.withAnnotation(Handles.class));

    for (Method method : methods) {
      Handles annotation = method.getAnnotation(Handles.class);
      EventHandlerInvoker invoker = new SimpleEventHandlerInvoker(annotation.value(), eventHandler, method);
      invokers.add(invoker);
    }

    return invokers;
  }
}