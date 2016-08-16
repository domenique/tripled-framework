package eu.tripledframework.eventbus.internal.domain;

import eu.tripledframework.eventbus.CommandDispatcher;
import eu.tripledframework.eventbus.EventPublisher;
import eu.tripledframework.eventbus.command.HelloCommand;
import eu.tripledframework.eventbus.event.TestEvent;
import eu.tripledframework.eventbus.handler.TestCommandHandler;
import eu.tripledframework.eventbus.handler.TestEventHandler;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.TestUnitOfWork;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.TestingUnitOfWorkFactory;
import eu.tripledframework.eventbus.internal.infrastructure.unitofwork.UnitOfWorkRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class CommandDispatchingUnitOfWorkTests extends AbstractEventBusTest {

  private CommandDispatcher commandDispatcher;
  private EventPublisher eventPublisher;
  private TestCommandHandler commandHandler;
  private TestingUnitOfWorkFactory unitOfWorkFactory;
  private TestEventHandler eventHandler;

  @Before
  public void setUp() throws Exception {
    unitOfWorkFactory = new TestingUnitOfWorkFactory();
    SynchronousEventBus eventBus = createSynchronousEventBus(Collections.emptyList(), unitOfWorkFactory);

    commandHandler = new TestCommandHandler();
    eventBus.subscribe(commandHandler);
    eventHandler = new TestEventHandler();
    eventBus.subscribe(eventHandler);

    commandDispatcher = eventBus;
    eventPublisher = eventBus;
  }

  @Test
  public void whenDispatching_shouldCreateAUnitOfWork() {
    // given
    HelloCommand helloCommand = new HelloCommand("Domenique");

    // given
    commandDispatcher.dispatch(helloCommand);

    // then
    assertThat(commandHandler.isHelloCommandHandled, is(true));
    assertThat(unitOfWorkFactory.getCreatedUoW(), notNullValue());
    assertThat(unitOfWorkFactory.getCreatedUoW().isCommitCalled, is(true));
  }

  @Test
  public void whenAUnitOfWorkExists_eventsShouldBeStoredInAUnitOfWork() {
    // given
    TestEvent testEvent = new TestEvent();
    TestUnitOfWork unitOfWork = new TestUnitOfWork();
    UnitOfWorkRepository.store(unitOfWork);

    // when
    eventPublisher.publish(testEvent);

    // then
    assertThat(eventHandler.testEventHandled, is(false));
    assertThat(unitOfWork.isCommitCalled, is(false));
    assertThat(unitOfWork.delayedEvents, hasItem(sameInstance(testEvent)));
  }

}
