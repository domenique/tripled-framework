package be.dticonsulting.support.command.application;

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
