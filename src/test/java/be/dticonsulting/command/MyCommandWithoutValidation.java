package be.dticonsulting.command;

import be.dticonsulting.command.Command;

class MyCommandWithoutValidation implements Command<Void> {

  public boolean isExecuteCalled;

  MyCommandWithoutValidation() {
  }

  @Override
  public Void execute() {
    isExecuteCalled = true;
    return null;
  }
}
