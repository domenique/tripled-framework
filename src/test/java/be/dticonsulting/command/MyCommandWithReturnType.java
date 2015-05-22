package be.dticonsulting.command;

import be.dticonsulting.command.Command;

class MyCommandWithReturnType implements Command<String> {

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
