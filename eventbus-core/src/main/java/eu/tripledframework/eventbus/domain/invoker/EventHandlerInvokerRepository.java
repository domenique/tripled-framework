/*
 * Copyright 2015 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        throw new DuplicateHandlerRegistrationException(String.format("An eventHandler with return type for event %s already exists.", invoker.getEventType()));
      }
    }
  }

  private Optional<EventHandlerInvoker> findByEventTypeWithReturnType(Class<?> eventType) {
    return eventHandlers.stream().filter(input -> input.handles(eventType) && input.hasReturnType()).findFirst();
  }

  public List<EventHandlerInvoker> findAllByEventType(Class<?> eventType) {
    return eventHandlers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
  }

  public Optional<EventHandlerInvoker> findByEventType(Class<?> eventType) {
    List<EventHandlerInvoker> found = eventHandlers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
    if (found.size() > 1) {
      throw new DuplicateHandlerFoundException(String.format("Found multiple handlers for %s. Expected only one.", eventType.getSimpleName()));
    }
    return found.stream().findFirst();
  }
}

