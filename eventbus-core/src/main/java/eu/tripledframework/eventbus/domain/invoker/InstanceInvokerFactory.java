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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.tripledframework.eventbus.domain.annotation.Handler;
import org.reflections.ReflectionUtils;

import eu.tripledframework.eventbus.domain.annotation.Handles;

import static org.reflections.ReflectionUtils.withAnnotation;

public class InstanceInvokerFactory implements InvokerFactory {

  @Override
  public List<Invoker> create(Object eventHandler) {
    List<Invoker> invokers = new ArrayList<>();

    Set<Method> methods = ReflectionUtils.getAllMethods(eventHandler.getClass(),
        withAnnotation(Handles.class));

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
}