package eu.tripledframework.eventbus.domain;

public interface EventSubscriber {

  void subscribe(Object eventHandler);
}
