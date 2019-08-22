/*
 * Copyright 2016 TripleD framework.
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

import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.TestInvokerFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EventBusTest extends AbstractEventBusTest {

  @Test
  void whenGivenAnEventHandlerInvokerFactory_shouldUseIt() throws Exception {
    // given
    TestInvokerFactory invokerFactory = new TestInvokerFactory();
    SynchronousEventBus eventBus = createSynchronousEventBus(Collections.emptyList(), Collections.singletonList(invokerFactory));

    // when
    eventBus.subscribe(new TestCommandHandler());

    // then
    assertThat(invokerFactory.isCreateCalled, is(true));
  }


}
