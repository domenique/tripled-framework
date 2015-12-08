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

package eu.tripledframework.eventstore.domain;

import java.util.Objects;
import java.util.UUID;

import org.joda.time.DateTime;

public class DomainEvent {

  private String id;
  private String aggregateRootIdentifier;
  private int revision;
  private DateTime timestamp;

  public DomainEvent(String aggregateRootIdentifier) {
    this.id = UUID.randomUUID().toString();
    this.aggregateRootIdentifier = aggregateRootIdentifier;
    this.timestamp = DateTime.now();
    this.revision = 0;
  }

  public DomainEvent(String aggregateRootIdentifier, int revision) {
    // TODO: Where should the revision come from ?
    // it should be an incrementing number this should not be in the domainEvent?
    this.id = UUID.randomUUID().toString();
    this.aggregateRootIdentifier = aggregateRootIdentifier;
    this.timestamp = DateTime.now();
    this.revision = revision;
  }

  protected DomainEvent() {
    // for frameworks
  }

  public String getId() {
    return id;
  }

  public String getAggregateRootIdentifier() {
    return aggregateRootIdentifier;
  }

  public int getRevision() {
    return revision;
  }

  public DateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DomainEvent other = (DomainEvent) obj;
    return Objects.equals(this.id, other.id);
  }
}
