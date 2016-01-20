package eu.tripledframework.eventbus.internal.infrastructure.invoker;

import eu.tripledframework.eventbus.internal.domain.Invoker;
import eu.tripledframework.eventbus.internal.domain.InvokerRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class InMemoryInvokerRepository implements InvokerRepository {

  private Set<Invoker> invokers;

  public InMemoryInvokerRepository() {
    this.invokers = new CopyOnWriteArraySet<>();
  }

  @Override
  public void add(Invoker invoker) {
    invokers.add(invoker);
  }


  @Override
  public List<Invoker> findAtLeastOneByEventType(Class<?> eventType) {
    List<Invoker> found = findAllByEventType(eventType);
    if (found != null && !found.isEmpty()) {
      return found;
    } else {
      throw new InvokerNotFoundException(String.format("Could not find an event handler for %s", eventType));
    }
  }

  @Override
  public Invoker getByEventType(Class<?> eventType) {
    Optional<Invoker> optional = findByEventType(eventType);
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new InvokerNotFoundException(String.format("Could not find an event handler for %s", eventType));
    }
  }

  private List<Invoker> findAllByEventType(Class<?> eventType) {
    return invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
  }

  private Optional<Invoker> findByEventType(Class<?> eventType) {
    List<Invoker> found = invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
    if (found.size() > 1) {
      throw new DuplicateInvokerFoundException(String
          .format("Found multiple handlers for %s. Expected only one.", eventType.getSimpleName()));
    }
    return found.stream().findFirst();
  }
}

