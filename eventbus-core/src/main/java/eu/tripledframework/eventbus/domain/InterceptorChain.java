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
 * The interceptorChain is used by an interceptor to proceed the chain.
 *
 * @param <ReturnType> The Type of the return object of the command.
 */
public interface InterceptorChain<ReturnType> {

  /**
   * Method which is supposed to be called by the interceptor the advance in the chain.
   *
   * @return The return object of the command.
   */
  ReturnType proceed();
}
