package eu.tripled.demo.application;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class HelloCommand {

  @NotBlank
  @NotNull
  private String name;

  public HelloCommand(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
