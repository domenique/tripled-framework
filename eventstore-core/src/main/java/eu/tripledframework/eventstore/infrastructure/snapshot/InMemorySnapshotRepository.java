package eu.tripledframework.eventstore.infrastructure.snapshot;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.tripledframework.eventstore.domain.snapshot.Snapshot;
import eu.tripledframework.eventstore.domain.snapshot.SnapshotRepository;

public class InMemorySnapshotRepository<AggregateRootType> implements SnapshotRepository<AggregateRootType> {

  private final List<Snapshot<AggregateRootType>> snapshots;

  public InMemorySnapshotRepository() {
    this.snapshots = new CopyOnWriteArrayList<>();
  }

  @Override
  public Optional<Snapshot<AggregateRootType>> findLatest(String identifier) {
    return snapshots.stream()
        .filter(s -> s.getIdentifier().equals(identifier))
        .reduce((s1, s2) -> s1.getRevision() > s2.getRevision() ? s1 : s2);
  }

  @Override
  public void save(Snapshot<AggregateRootType> snapshot) {
    snapshots.add(snapshot);
  }
}
