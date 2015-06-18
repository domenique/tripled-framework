package eu.tripled.demo.application;

import javax.validation.constraints.Size;

public class HelloCommand {

  @Size(min = 2)
  private String name;

  public HelloCommand(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
