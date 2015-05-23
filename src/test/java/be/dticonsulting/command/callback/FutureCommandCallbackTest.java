package be.dticonsulting.command.callback;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class FutureCommandCallbackTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void whenCancelCalled_shouldReturnFalse() throws Exception {
    // given
    FutureCommandCallback future = new FutureCommandCallback();

    // when
    boolean cancel = future.cancel(true);

    // then
    assertThat(cancel).isEqualTo(false);
  }

  @Test
  public void whenIsCancelledCalled_shouldReturnFalse() throws Exception {
    // given
    FutureCommandCallback future = new FutureCommandCallback();

    // when
    boolean cancel = future.isCancelled();

    // then
    assertThat(cancel).isEqualTo(false);
  }

  @Test
  public void whenGetWithTimeoutCalled_shouldTimeout() throws Exception {
    // given
    FutureCommandCallback future = new FutureCommandCallback();

    expectedException.expect(TimeoutException.class);

    // when
    future.get(1, TimeUnit.MILLISECONDS);

    // then -> Exception

  }

  @Test
  public void whenGetWithTimeoutCalled_shouldNotTimeoutIfResponseAvailable() throws Exception {
    // given
    FutureCommandCallback<String> future = new FutureCommandCallback();
    future.onSuccess("response");

    // when
    String response = future.get(1, TimeUnit.MILLISECONDS);

    // then
    assertThat(response).isEqualTo("response"); 

  }
}