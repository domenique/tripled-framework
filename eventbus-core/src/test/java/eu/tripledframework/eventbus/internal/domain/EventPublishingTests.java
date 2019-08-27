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
        SynchronousEventBus eventBus = createSynchronousEventBus(Collections.singletonList(new LoggingEventBusInterceptor()));

        eventHandler = new TestCommandHandler();
        eventBus.subscribe(eventHandler);

        eventPublisher = eventBus;
    }

    @Test
    void whenGivenAnEventWithMultipleHandlers_allHandlersShouldBeInvoked() throws Exception {
        // given
        AnCommandHandledByMultipleHandlers event = new AnCommandHandledByMultipleHandlers();

        // when
        eventPublisher.publish(event);

        // then
        assertThat(eventHandler.handledByFirstCount, equalTo(1));
        assertThat(eventHandler.handledBySecondCount, equalTo(1));
    }

    @Test
    void whenGivenAnEventWithNoHandlers_shouldNotThrowException() {
        // given
        UnhandledEvent unhandledEvent = new UnhandledEvent();

        // when
        eventPublisher.publish(unhandledEvent);

        // then -> no exception, warning is logged.

    }

}
