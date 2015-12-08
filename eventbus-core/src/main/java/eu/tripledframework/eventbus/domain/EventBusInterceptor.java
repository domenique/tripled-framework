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
package eu.tripledframework.eventbus.domain;

/**
 * Interface which defines an interceptor which is being called by the EventBus when executing a command.
 * <p>
 * The interceptor contract defines one method: intercept(). This method is invoked by the InterceptorChain which is
 * passed into the method as the first argument. Implementations are required to call the InterceptorChain.proceed() method
 * to complete the chain. Note that any interceptor in the chain should handle exceptions by either swallowing them in order to complete
 * the chain or to rethrow them to jump out of the chain.
 * <p>
 * The event passed to the intercept method is the event that will eventually be executed.
 */
public interface EventBusInterceptor {

  /**
   * Intercepts the event dispatching.
   * <p>
   * This method works similarly to an Around Advice. Implementations are supposed to call the chain.proceed() method to
   * advance further in the chain.
   *
   * @param chain        The InterceptorChain which is being applied. The implementation is supposed to call the proceed()
   *                     method when it wishes to advance in the chain
   * @param event        The event which will eventually be executed.
   * @param <ReturnType> The return type of the event execution and by consequence this Interceptor.
   * @return The return object from the chain.proceed() command or, depending on the implementation, some other instance of the ReturnType.
   */
  <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event);

}
