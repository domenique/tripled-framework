package eu.tripledframework.eventbus.command;

import javax.validation.constraints.NotNull;

public class ValidatingCommand {

  @NotNull
  private String message;

  public ValidatingCommand(String message) {
    this.message = message;
  }
}
