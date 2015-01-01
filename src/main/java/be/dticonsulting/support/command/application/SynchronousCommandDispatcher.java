package be.dticonsulting.support.command.application;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synchronous implementation of the CommandDispatcher.
 */
@Service
public class SynchronousCommandDispatcher implements CommandDispatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(SynchronousCommandDispatcher.class);

  @Override
  public void dispatch(Command command) {
    Assert.notNull(command, "The command cannot be null.");
    LOGGER.debug("Received a command to dispatch: {}", command.getClass().getSimpleName());

    boolean isValid = true;
    if (shouldPerformValidation(command)) {
      LOGGER.debug("Validating command {}", command.getClass().getSimpleName());
      isValid = validate(command);
    }

    if (isValid) {
      LOGGER.debug("Executing command {}", command.getClass().getSimpleName());
      command.execute();
    } else {
      throw new CommandValidationException();
    }

    LOGGER.debug("Finished executing command {}", command.getClass().getSimpleName());
  }

  private boolean validate(Command command) {
    return ((Validateable) command).validate();
  }

  private boolean shouldPerformValidation(Command command) {
    return command instanceof Validateable;
  }
}
