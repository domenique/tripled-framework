package eu.tripledframework.eventbus.domain.invoker;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class InvokerRepository {

  private Set<Invoker> invokers;

  public InvokerRepository() {
    this.invokers = new CopyOnWriteArraySet<>();
  }

  public void add(Invoker invoker) {
    invokers.add(invoker);
  }


  public List<Invoker> findAllByEventType(Class<?> eventType) {
    return invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
  }

  public Optional<Invoker> findByEventType(Class<?> eventType) {
    List<Invoker> found = invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
    if (found.size() > 1) {
      throw new DuplicateInvokerFoundException(String.format("Found multiple handlers for %s. Expected only one.", eventType.getSimpleName()));
    }
    return found.stream().findFirst();
  }
}

