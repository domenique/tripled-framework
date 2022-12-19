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
package eu.tripledframework.eventbus.internal.infrastructure.callback;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


class FutureCommandCallbackTest {

    @Test
    void whenCancelCalled_shouldReturnFalse() throws Exception {
        // given
      var future = new FutureCommandCallback();

        // when
      var cancel = future.cancel(true);

        // then
        assertThat(cancel, is(false));
    }

    @Test
    void whenIsCancelledCalled_shouldReturnFalse() throws Exception {
        // given
      var future = new FutureCommandCallback();

        // when
      var cancel = future.isCancelled();

        // then
        assertThat(cancel, is(false));
    }

    @Test
    void whenGetWithTimeoutCalled_shouldTimeout() {
        // given
      var future = new FutureCommandCallback();

        // when
        Assertions.assertThrows(TimeoutException.class, () -> future.get(1, TimeUnit.MILLISECONDS));

        // then -> Exception

    }

    @Test
    void whenGetWithTimeoutCalled_shouldNotTimeoutIfResponseAvailable() throws Exception {
        // given
      var future = new FutureCommandCallback<String>();
        future.onSuccess("response");

        // when
      var response = future.get(1, TimeUnit.MILLISECONDS);

        // then
        assertThat(response, equalTo("response"));

    }
}