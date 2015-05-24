package eu.tripled.command;

import eu.tripled.command.Command;

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
