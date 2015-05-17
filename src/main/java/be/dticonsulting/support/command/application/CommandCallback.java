package be.dticonsulting.support.command.application;

public interface CommandCallback<ReturnType> {

  void onSuccess(ReturnType result);

  void onValidationFailure(Command<ReturnType> command);

  void onFailure(Throwable exception);

}
