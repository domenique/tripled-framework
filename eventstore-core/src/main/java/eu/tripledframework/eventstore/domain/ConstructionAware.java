package eu.tripledframework.eventstore.domain;

/**
 * Marker interface that provides hooks into the reconstruction process to perform specific actions that the domain entity requires.
 */
public interface ConstructionAware {

  /**
   * Method which will be called after the reconstruction process. If this method is called, the object is fully reconstructed the full
   * object state is available.
   */
  void postConstruct();
}
