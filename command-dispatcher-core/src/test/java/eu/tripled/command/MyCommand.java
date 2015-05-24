package eu.tripled.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCommand implements Command<Void>, Validateable {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyCommand.class);

  public boolean isValidateCalled;
  public boolean isExecuteCalled;
  private boolean validationOutcome;
  public String threadNameForExecute;
  public String threadNameForValidate;

  public MyCommand(boolean validationOutcome) {
    this.validationOutcome = validationOutcome;
  }

  @Override
  public boolean validate() {
    LOGGER.debug("Validation called.");
    threadNameForValidate = Thread.currentThread().getName();
    isValidateCalled = true;
    return validationOutcome;
  }

  @Override
  public Void execute() {
    LOGGER.debug("Executing command.");
    threadNameForExecute = Thread.currentThread().getName();
    isExecuteCalled = true;
    return null;
  }
}
