package eu.tripledframework.eventbus.domain.invoker;

import java.util.List;

public interface EventHandlerInvokerFactory {

  List<EventHandlerInvoker> create(Object eventHandler);
}
