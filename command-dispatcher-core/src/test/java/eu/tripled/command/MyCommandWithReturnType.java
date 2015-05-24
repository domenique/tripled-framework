package eu.tripled.command;

import eu.tripled.command.Command;

public class MyCommandWithReturnType implements Command<String> {

  public boolean isExecuteCalled;
  private String response;

  public MyCommandWithReturnType(String response) {
    this.response = response;
  }

  @Override
  public String execute() {
    isExecuteCalled = true;
    return response;
  }
}
