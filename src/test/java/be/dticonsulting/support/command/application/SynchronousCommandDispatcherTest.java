package be.dticonsulting.support.command.application;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class SynchronousCommandDispatcherTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SynchronousCommandDispatcher synchronousCommandDispatcher;

    @Before
    public void setUp() throws Exception {
        synchronousCommandDispatcher = new SynchronousCommandDispatcher();
    }

    @Test
    public void whenGivenNothing_shouldThrowException() throws Exception {
        // given
        Command nullCommand = null;

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The command cannot be null.");

        // when
        synchronousCommandDispatcher.dispatch(nullCommand);

        // then -> Exception
    }

    @Test
    public void whenGivenAValidCommand_shouldValidateAndExecute() throws Exception {
        // when
        MyCommand command = new MyCommand(true);

        // when
        synchronousCommandDispatcher.dispatch(command);

        // then
        assertThat(command.isValidateCalled).isEqualTo(true);
        assertThat(command.isExecuteCalled).isEqualTo(true);
    }

    @Test
    public void whenGivenAValidCommandThatShouldNotBeValidated_shouldExecuteCommand() throws Exception {
        // when
        MyCommandWithoutValidation command = new MyCommandWithoutValidation();

        // when
        synchronousCommandDispatcher.dispatch(command);

        // then
        assertThat(command.isExecuteCalled).isEqualTo(true);
    }

    @Test
    public void whenGivenAnInvalidCommand_shouldThrowException() throws Exception {
        // when
        MyCommand command = new MyCommand(false);

        // when
        Throwable exception = null;
        try {
            synchronousCommandDispatcher.dispatch(command);
        } catch (Exception e) {
            exception = e;
        }

        // then
        assertThat(command.isValidateCalled).isEqualTo(true);
        assertThat(exception).isInstanceOf(CommandValidationException.class);
        assertThat(command.isExecuteCalled).isEqualTo(false);
    }

    private class MyCommand implements Command, Validateable {

        private boolean isValidateCalled;
        private boolean isExecuteCalled;
        private boolean validationOutcome;

        private MyCommand(boolean validationOutcome) {
            this.validationOutcome = validationOutcome;
        }

        @Override
        public boolean validate() {
            isValidateCalled = true;
            return validationOutcome;
        }

        @Override
        public void execute() {
            isExecuteCalled = true;
        }
    }

    private class MyCommandWithoutValidation implements Command {

        private boolean isExecuteCalled;

        private MyCommandWithoutValidation() {
        }

        @Override
        public void execute() {
            isExecuteCalled = true;
        }
    }
}