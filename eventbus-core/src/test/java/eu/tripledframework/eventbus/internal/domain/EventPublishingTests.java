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

package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.command.AnCommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.event.UnhandledEvent;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class EventPublishingTests extends EventBusTest {

    private EventPublisher eventPublisher;
    private TestCommandHandler eventHandler;

    @BeforeEach
    void setUp() throws Exception {
      var eventBus = createSynchronousEventBus(Collections.singletonList(new LoggingEventBusInterceptor()));

        eventHandler = new TestCommandHandler();
        eventBus.subscribe(eventHandler);

        eventPublisher = eventBus;
    }

    @Test
    void whenGivenAnEventWithMultipleHandlers_allHandlersShouldBeInvoked() throws Exception {
        // given
      var event = new AnCommandHandledByMultipleHandlers();

        // when
        eventPublisher.publish(event);

        // then
        assertThat(eventHandler.handledByFirstCount, equalTo(1));
        assertThat(eventHandler.handledBySecondCount, equalTo(1));
    }

    @Test
    void whenGivenAnEventWithNoHandlers_shouldNotThrowException() {
        // given
      var unhandledEvent = new UnhandledEvent();

        // when
        eventPublisher.publish(unhandledEvent);

        // then -> no exception, warning is logged.

    }

}
