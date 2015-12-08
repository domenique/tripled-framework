/*
 * Copyright 2015 TripleD, DTI-Consulting.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
