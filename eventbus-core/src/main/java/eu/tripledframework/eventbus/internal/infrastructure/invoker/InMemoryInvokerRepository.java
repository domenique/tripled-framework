/*
 * Copyright 2022 TripleD framework.
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
    public Invoker getByEventType(Class<?> eventType) {
        return findByEventType(eventType)
                .orElseThrow(() -> new InvokerNotFoundException(String.format("Could not find an event handler for %s", eventType)));
    }

    @Override
    public List<Invoker> findAllByEventType(Class<?> eventType) {
        return invokers.stream()
                .filter(p -> p.handles(eventType))
                .collect(Collectors.toList());
    }

    private Optional<Invoker> findByEventType(Class<?> eventType) {
      var found = invokers.stream().filter(p -> p.handles(eventType)).collect(Collectors.toList());
        if (found.size() > 1) {
            throw new DuplicateInvokerFoundException(String
                    .format("Found multiple handlers for %s. Expected only one.", eventType.getSimpleName()));
        }
        return found.stream().findFirst();
    }
}

