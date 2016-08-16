package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.command.AnCommandHandledByMultipleHandlers;
import eu.tripledframework.eventbus.event.UnhandledEvent;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.internal.infrastructure.interceptor.LoggingEventBusInterceptor;
import eu.tripledframework.eventbus.internal.infrastructure.invoker.InvokerNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class EventPublishingTests extends EventBusTest {

  private EventPublisher eventPublisher;
  private TestCommandHandler eventHandler;

  @Before
  public void setUp() throws Exception {
    SynchronousEventBus eventBus = createSynchronousEventBus(Collections.singletonList(new LoggingEventBusInterceptor()));

    eventHandler = new TestCommandHandler();
    eventBus.subscribe(eventHandler);

    eventPublisher = eventBus;
  }

  @Test
  public void whenGivenAnEventWithMultipleHandlers_allHandlersShouldBeInvoked() throws Exception {
    // given
    AnCommandHandledByMultipleHandlers event = new AnCommandHandledByMultipleHandlers();

    // when
    eventPublisher.publish(event);

    // then
    assertThat(eventHandler.handledByFirstCount, equalTo(1));
    assertThat(eventHandler.handledBySecondCount, equalTo(1));
  }

  @Test(expected = InvokerNotFoundException.class)
  public void whenGivenAnEventWithNoHandlers_shouldThrowException() throws Exception {
    // given
    UnhandledEvent unhandledEvent = new UnhandledEvent();

    // when
    eventPublisher.publish(unhandledEvent);

    // then -> exception
  }

}
