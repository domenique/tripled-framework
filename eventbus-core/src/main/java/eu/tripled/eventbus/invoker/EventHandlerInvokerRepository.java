package eu.tripled.eventbus.invoker;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventHandlerInvokerRepository {

  private Set<EventHandlerInvoker> eventHandlers;

  public EventHandlerInvokerRepository() {
    this.eventHandlers = new CopyOnWriteArraySet<>();
  }

  public void addEventHandlerInvoker(EventHandlerInvoker invoker) {
    if (!eventHandlers.contains(invoker)) {
      if (invoker.hasReturnType() && !findByEventWithReturnType(invoker.getEventType()).isPresent() || !invoker.hasReturnType()) {
        eventHandlers.add(invoker);
      } else if (invoker.hasReturnType() && findByEventWithReturnType(invoker.getEventType()).isPresent()) {
        throw new DuplicateEventHandlerRegistrationException(String.format("An eventHandler with return type for event %s already exists.", invoker.getEventType()));
      }
    }
  }

  public List<EventHandlerInvoker> findAllByEventWithoutReturnType(Class<?> eventType) {
    return Lists.newArrayList(Iterables.filter(eventHandlers, input -> (input.handles(eventType) && !input.hasReturnType())));
  }

  public Optional<EventHandlerInvoker> findByEventWithReturnType(Class<?> eventType) {
    return Iterables.tryFind(eventHandlers, input -> input.handles(eventType) && input.hasReturnType());
  }
}

