package be.dticonsulting.command.command;

import be.dticonsulting.command.Command;

public class MyCommandWithoutValidation implements Command<Void> {

  public boolean isExecuteCalled;

  public MyCommandWithoutValidation() {
  }

  @Override
  public Void execute() {
    isExecuteCalled = true;
    return null;
  }
}
