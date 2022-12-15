/*
 * Copyright 2022 TripleD framework.
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
