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
package eu.tripledframework.eventbus.internal.infrastructure.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import eu.tripledframework.eventbus.internal.domain.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimpleInvoker implements Invoker {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleInvoker.class);

  private final Class<?> eventType;
  private final Object eventHandler;
  private final Method method;

  public SimpleInvoker(Class eventType, Object eventHandler, Method eventHandlerMethod) {
    this.eventType = eventType;
    this.eventHandler = eventHandler;
    this.method = eventHandlerMethod;
  }

  @Override
  public boolean handles(Class<?> eventTypeToHandle) {
    return this.eventType.isAssignableFrom(eventTypeToHandle);
  }

  @Override
  public boolean hasReturnType() {
    return !method.getReturnType().getName().equals("void");
  }

  @Override
  public Object invoke(Object object) {
    LOGGER.debug("About to invoke {}.{}() with event {}", eventHandler.getClass().getSimpleName(), method.getName(), object);
    try {
      return method.invoke(eventHandler, object);
    } catch (IllegalAccessException e) {
      String errorMsg = String.format("Could not invoke Handler method %s on %s", method.getName(), eventHandler.getClass().getSimpleName());
      throw new InvocationException(errorMsg, e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new InvocationException(
            "The invocation of the event handler threw an unknown checked exception.", e);
      }
    }
  }

  @Override
  public String toString() {
    return "Invoker{" +
           "eventType=" + eventType +
           ", eventHandler=" + eventHandler +
           ", method=" + method +
           '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventType, eventHandler, method);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SimpleInvoker other = (SimpleInvoker) obj;
    return Objects.equals(this.eventType, other.eventType)
        && Objects.equals(this.eventHandler, other.eventHandler)
        && Objects.equals(this.method, other.method);
  }
}
