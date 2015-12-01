package eu.tripledframework.eventstore.domain.snapshot;

import java.util.Optional;

public interface SnapshotRepository<AggregateRootType> {

  Optional<Snapshot<AggregateRootType>> findLatest(String identifier);

  void save(Snapshot<AggregateRootType> snapshot);
}
