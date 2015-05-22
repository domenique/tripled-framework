package be.dticonsulting.command;

class MyCommandWhichFailsWithException extends MyCommand {

  public MyCommandWhichFailsWithException(boolean validationOutcome) {
    super(validationOutcome);
  }

  @Override
  public Void execute() {
    throw new IllegalStateException("command failed.");
  }
}
