package eu.tripled.command;

public class ValidatingCommand implements Validateable {

  private boolean validationOutcome = false;
  public boolean isValidateCalled = false;

  public ValidatingCommand(boolean validationOutcome) {
    this.validationOutcome = validationOutcome;
  }

  @Override
  public boolean validate() {
    isValidateCalled = true;
    return validationOutcome;
  }
}
