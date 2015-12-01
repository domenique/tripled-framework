package eu.tripledframework.eventstore.domain.snapshot;

public class Snapshot<AggregateRootType> {

  private AggregateRootType aggregateRoot;
  private int revision;
  private String identifier;

  public Snapshot(AggregateRootType aggregateRoot, String identifier, int revision) {
    this.aggregateRoot = aggregateRoot;
    this.identifier = identifier;
    this.revision = revision;
  }

  public int getRevision() {
    return revision;
  }

  public String getIdentifier() {
    return identifier;
  }

  public AggregateRootType getAggregateRoot() {
    return aggregateRoot;
  }
}
