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

import eu.tripledframework.eventbus.Handler;
import eu.tripledframework.eventbus.Handles;
import eu.tripledframework.eventbus.internal.domain.Invoker;
import eu.tripledframework.eventbus.internal.domain.InvokerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleInvokerFactory implements InvokerFactory {

  @Override
  public List<Invoker> create(Object eventHandler) {
    List<Invoker> invokers = new ArrayList<>();

    Set<Method> methods = getMethodsWithAnotation(eventHandler.getClass(), Handles.class);


    for (Method method : methods) {
      Handles annotation = method.getAnnotation(Handles.class);
      Invoker invoker = new SimpleInvoker(annotation.value(), eventHandler, method);
      invokers.add(invoker);
    }

    return invokers;
  }

  @Override
  public boolean supports(Object object) {
    Handler annotation = object.getClass().getAnnotation(Handler.class);

    return annotation != null;
  }

  private Set<Method> getMethodsWithAnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
    Set<Method> methods = Arrays.stream(clazz.getMethods())
                                .filter(c -> c.isAnnotationPresent(annotation))
                                .collect(Collectors.toSet());

    // If we are dealing with a proxied object, we should look at the superClass
    // as well because cglib does not propagate annotations.
    if (methods == null || methods.isEmpty()) {
      methods = Arrays.stream(clazz.getSuperclass().getMethods())
                      .filter(c -> c.isAnnotationPresent(annotation))
                      .collect(Collectors.toSet());
    }
    return methods;
  }
}