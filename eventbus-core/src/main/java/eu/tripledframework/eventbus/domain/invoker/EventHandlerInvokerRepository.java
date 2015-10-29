package eu.tripledframework.eventbus.domain.invoker;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class EventHandlerInvokerRepository {

  private Set<EventHandlerInvoker> eventHandlers;

  public EventHandlerInvokerRepository() {
    this.eventHandlers = new CopyOnWriteArraySet<>();
  }

  public void addEventHandlerInvoker(EventHandlerInvoker invoker) {
    if (!eventHandlers.contains(invoker)) {
      if (invoker.hasReturnType() && !findByEventTypeWithReturnType(invoker.getEventType()).isPresent() || !invoker.hasReturnType()) {
        eventHandlers.add(invoker);
      } else if (invoker.hasReturnType() && findByEventTypeWithReturnType(invoker.getEventType()).isPresent()) {
        throw new DuplicateEventHandlerRegistrationException(String.format("An eventHandler with return type for event %s already exists.", invoker.getEventType()));
      }
    }
  }

  public List<EventHandlerInvoker> findAllByEventTypeWithoutReturnType(Class<?> eventType) {
    return eventHandlers.stream()
        .filter(p -> p.handles(eventType) && !p.hasReturnType())
        .collect(Collectors.toList());
  }

  public Optional<EventHandlerInvoker> findByEventTypeWithReturnType(Class<?> eventType) {
    return eventHandlers.stream()
        .filter(input -> input.handles(eventType) && input.hasReturnType())
        .findFirst();

  }
}

