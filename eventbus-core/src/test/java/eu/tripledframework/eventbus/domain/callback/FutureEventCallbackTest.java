/*
 * Copyright 2015 TripleD, DTI-Consulting.
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

package eu.tripledframework.eventbus.domain.callback;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class FutureEventCallbackTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void whenCancelCalled_shouldReturnFalse() throws Exception {
    // given
    FutureEventCallback future = new FutureEventCallback();

    // when
    boolean cancel = future.cancel(true);

    // then
    assertThat(cancel, is(false));
  }

  @Test
  public void whenIsCancelledCalled_shouldReturnFalse() throws Exception {
    // given
    FutureEventCallback future = new FutureEventCallback();

    // when
    boolean cancel = future.isCancelled();

    // then
    assertThat(cancel, is(false));
  }

  @Test
  public void whenGetWithTimeoutCalled_shouldTimeout() throws Exception {
    // given
    FutureEventCallback future = new FutureEventCallback();

    expectedException.expect(TimeoutException.class);

    // when
    future.get(1, TimeUnit.MILLISECONDS);

    // then -> Exception

  }

  @Test
  public void whenGetWithTimeoutCalled_shouldNotTimeoutIfResponseAvailable() throws Exception {
    // given
    FutureEventCallback<String> future = new FutureEventCallback();
    future.onSuccess("response");

    // when
    String response = future.get(1, TimeUnit.MILLISECONDS);

    // then
    assertThat(response, equalTo("response"));

  }
}