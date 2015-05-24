package eu.tripled.command;

/**
 * Interface which should be implemented by Command implementations if they require a validation step before being executed.
 */
public interface Validateable {

  /**
   * Method which is called by the CommandDispatcher to perform validation.
   * <p/>
   * Note that this method should not throw any exception, if the validation should fail, then it is up to the CommandDispatcher to throw
   * an exception.
   *
   * @return <code>true</code> if the validation passed, <code>false</code> otherwise.
   */
  boolean validate();
}
