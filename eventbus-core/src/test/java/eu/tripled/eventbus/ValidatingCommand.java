package eu.tripled.eventbus;

import javax.validation.constraints.NotNull;

public class ValidatingCommand {

  @NotNull
  private String message;

  public ValidatingCommand(String message) {
    this.message = message;
  }
}
