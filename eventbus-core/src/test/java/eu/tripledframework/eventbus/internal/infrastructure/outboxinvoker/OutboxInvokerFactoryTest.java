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

package eu.tripledframework.eventbus.internal.infrastructure.outboxinvoker;

import eu.tripledframework.eventbus.handler.SecondTestCommandHandler;
import eu.tripledframework.eventbus.handler.TestEventHandler;
import eu.tripledframework.eventbus.internal.domain.InvokerFactory;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.SimpleInvokerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class OutboxInvokerFactoryTest {

  private InvokerFactory factory;

  @BeforeEach
  void setUp() {
    factory = OutboxInvokerFactory.decorate(new SimpleInvokerFactory());
  }

  @Test
  void shouldReturnDecoratedInvoker() {
    var invokers = factory.create(new TestEventHandler());

    assertThat(invokers, hasItems(instanceOf(OutboxInvokerDecorator.class)));
  }

  @Test
  void supportsIsDelegated() {
    var supports = factory.supports(new TestEventHandler());

    assertThat(supports, is(true));
  }

  @Test
  void supportReturnsFalseIfDelegateReturnsFalse() {
    var supports = factory.supports(new String());

    assertThat(supports, is(false));
  }

  @Test
  void returnsEmptyIfNoHandlers() {
    var invokers = factory.create(new String());

    assertThat(invokers, empty());
  }

  @Test
  void returnsNonDecoratedIfAllHandlerHaveResponse() {
    var invokers = factory.create(new SecondTestCommandHandler());

    assertThat(invokers, hasSize(1));
    assertThat(invokers, hasItems(not(instanceOf(OutboxInvokerDecorator.class))));
  }
}
