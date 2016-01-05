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
package eu.tripledframework.eventbus.domain.interceptor;

import eu.tripledframework.eventbus.domain.EventBusInterceptor;
import eu.tripledframework.eventbus.domain.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class LoggingEventBusInterceptor implements EventBusInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventBusInterceptor.class);

  @Override
  public <ReturnType> ReturnType intercept(InterceptorChain<ReturnType> chain, Object event) {
    LOGGER.debug("Executing command {}", event.getClass().getSimpleName());
    Instant start = Instant.now();
    try {
      ReturnType proceed = chain.proceed();
      LOGGER.debug("Finished executing command {}.", event.getClass().getSimpleName());
      return proceed;
    } catch (Throwable ex) {
      LOGGER.debug("Command {} failed", event.getClass().getSimpleName());
      throw ex;
    } finally {
      Instant end = Instant.now();
      LOGGER.debug("Execution of {} took {}ms", event.getClass().getSimpleName(),
          Duration.between(start, end).toMillis());
    }
  }
}