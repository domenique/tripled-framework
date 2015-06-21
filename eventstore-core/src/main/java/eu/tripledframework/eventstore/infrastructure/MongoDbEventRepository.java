package eu.tripledframework.eventstore.infrastructure;

import com.mongodb.Mongo;
import eu.tripledframework.eventstore.domain.Event;
import eu.tripledframework.eventstore.domain.EventRepository;

import java.util.Collection;
import java.util.Collections;

public class MongoDbEventRepository implements EventRepository {

  private final Mongo mongo;

  public MongoDbEventRepository(Mongo mongo) {
    this.mongo = mongo;
  }

  @Override
  public Collection<Event> findAllById(String id) {
    return Collections.EMPTY_LIST;
  }

  @Override
  public void save(Event object) {

  }
}
