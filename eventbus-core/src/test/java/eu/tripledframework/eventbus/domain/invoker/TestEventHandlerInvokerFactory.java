package eu.tripledframework.eventbus.domain.invoker;

import java.util.Collections;
import java.util.List;

public class TestEventHandlerInvokerFactory implements EventHandlerInvokerFactory {

  public boolean isCreateCalled;

  @Override
  public List<EventHandlerInvoker> create(Object eventHandler) {
    isCreateCalled = true;
    return Collections.emptyList();
  }

  @Override
  public boolean supports(Object object) {
    return true;
  }
}
