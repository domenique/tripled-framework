package be.dticonsulting.command;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.Validateable;

class MyCommand implements Command<Void>, Validateable {

  public boolean isValidateCalled;
  public boolean isExecuteCalled;
  private boolean validationOutcome;

  MyCommand(boolean validationOutcome) {
    this.validationOutcome = validationOutcome;
  }

  @Override
  public boolean validate() {
    isValidateCalled = true;
    return validationOutcome;
  }

  @Override
  public Void execute() {
    isExecuteCalled = true;
    return null;
  }
}
