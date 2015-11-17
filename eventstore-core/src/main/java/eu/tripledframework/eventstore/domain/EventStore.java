package eu.tripledframework.eventstore.domain;

import java.util.Collection;
import java.util.Optional;

import eu.tripledframework.eventstore.domain.snapshot.Snapshot;
import eu.tripledframework.eventstore.domain.snapshot.SnapshotRepository;
import eu.tripledframework.eventstore.infrastructure.ReflectionObjectConstructor;

public class EventStore<AggregateRootType> {

  private Class<AggregateRootType> type;
  private final EventRepository eventRepository;
  private final SnapshotRepository<AggregateRootType> snapshotRepository;
  private final ReflectionObjectConstructor<AggregateRootType> objectConstructor;

  public EventStore(Class<AggregateRootType> type, EventRepository eventRepository,
                    SnapshotRepository<AggregateRootType> snapshotRepository) {
    this.type = type;
    this.eventRepository = eventRepository;
    this.snapshotRepository = snapshotRepository;
    this.objectConstructor = new ReflectionObjectConstructor<>(type);
  }

  public Optional<AggregateRootType> findById(String identifier) {
    Optional<Snapshot<AggregateRootType>> retrievedSnapshot = snapshotRepository.findLatest(identifier);

    if (retrievedSnapshot.isPresent()) {
      Snapshot<AggregateRootType> snapshot = retrievedSnapshot.get();
      Collection<DomainEvent> allEvents =
          eventRepository.findAllByIdAndAfterRevision(identifier, snapshot.getRevision());

      return Optional.ofNullable(objectConstructor.applyDomainEvents(snapshot.getAggregateRoot(), allEvents));
    } else {
      Collection<DomainEvent> allEvents = eventRepository.findAllById(identifier);

      return Optional.ofNullable(objectConstructor.construct(allEvents));
    }
  }
}
