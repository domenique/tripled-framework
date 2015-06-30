package eu.tripledframework.eventstore.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DomainEventTest {

  @Test
  public void whenCreating_shouldInstantiateTimestampAndId() throws Exception {
    // given
    String sourceIdentifier = "sourceIdentifier";

    // when
    DomainEvent event = new DomainEvent(sourceIdentifier);

    // then
    assertThat(event.getTimestamp(), notNullValue());
    assertThat(event.getId(), notNullValue());
    assertThat(event.getAggregateRootIdentifier(), equalTo(sourceIdentifier));
  }

  @Test
  public void whenCreatingWithProtectedConstructor_shouldNotInstantiateTimestampAndId() throws Exception {
    // given

    // when
    DomainEvent event = new DomainEvent();

    // then
    assertThat(event.getTimestamp(), nullValue());
    assertThat(event.getId(), nullValue());
    assertThat(event.getAggregateRootIdentifier(), nullValue());
  }

  @Test
  public void whenCreating_ShouldNotBeEqual() throws Exception {
    // given
    String sourceIdentifier = "sourceIdentifier";
    DomainEvent event1 = new DomainEvent(sourceIdentifier);
    DomainEvent event2 = new DomainEvent(sourceIdentifier);

    // when
    boolean response = event1.equals(event2);

    // then
    assertThat(response, is(false));
  }

  @Test
  public void whenComparingWithNull_ShouldNotBeEqual() throws Exception {
    // given
    String sourceIdentifier = "sourceIdentifier";
    DomainEvent event1 = new DomainEvent(sourceIdentifier);

    // when
    boolean response = event1.equals(null);

    // then
    assertThat(response, is(false));
  }

  @Test
  public void whenComparingSameInstance_ShouldBeEqual() throws Exception {
    // given
    String sourceIdentifier = "sourceIdentifier";
    DomainEvent event1 = new DomainEvent(sourceIdentifier);

    // when
    boolean response = event1.equals(event1);

    // then
    assertThat(response, is(true));
  }

  @Test
  public void whenCreatingHashOfTwoObject_ShouldNotBeEqual() throws Exception {
    // given
    String sourceIdentifier = "sourceIdentifier";
    DomainEvent event1 = new DomainEvent(sourceIdentifier);
    DomainEvent event2 = new DomainEvent(sourceIdentifier);

    // when
    int hashForEvent1 = event1.hashCode();
    int hashForEvent2 = event2.hashCode();

    // then
    assertThat(hashForEvent1, not(equalTo(hashForEvent2)));
  }
}