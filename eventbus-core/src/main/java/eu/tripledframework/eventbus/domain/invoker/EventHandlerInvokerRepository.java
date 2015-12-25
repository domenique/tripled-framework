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

  private Set<HandlerInvoker> invokers;

  public EventHandlerInvokerRepository() {
    this.invokers = new CopyOnWriteArraySet<>();
  }

  public void addEventHandlerInvoker(HandlerInvoker invoker) {
    if (!invokers.contains(invoker)) {
      if (invoker.hasReturnType() && !findByEventTypeWithReturnType(invoker.getEventType()).isPresent() || !invoker.hasReturnType()) {
        invokers.add(invoker);
      } else if (invoker.hasReturnType() && findByEventTypeWithReturnType(invoker.getEventType()).isPresent()) {
        throw new DuplicateHandlerRegistrationException(String.format("An eventHandler with return type for event %s already exists.", invoker.getEventType()));
      }
    }
  }

  private Optional<HandlerInvoker> findByEventTypeWithReturnType(Class<?> eventType) {
    return invokers.stream().filter(input -> input.handles(eventType) && input.hasReturnType()).findFirst();
  }

  public List<HandlerInvoker> findAllByEventType(Class<?> eventType) {
    return invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
  }

  public Optional<HandlerInvoker> findByEventType(Class<?> eventType) {
    List<HandlerInvoker> found = invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
    if (found.size() > 1) {
      throw new DuplicateHandlerFoundException(String.format("Found multiple handlers for %s. Expected only one.", eventType.getSimpleName()));
    }
    return found.stream().findFirst();
  }
}

