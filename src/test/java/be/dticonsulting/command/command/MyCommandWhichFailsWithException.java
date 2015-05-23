package be.dticonsulting.command.command;

public class MyCommandWhichFailsWithException extends MyCommand {

  public MyCommandWhichFailsWithException(boolean validationOutcome) {
    super(validationOutcome);
  }

  @Override
  public Void execute() {
    super.execute();
    throw new IllegalStateException("command failed.");
  }
}
