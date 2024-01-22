/*
 * Copyright (C) 2024 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package hu.aestallon.bigcounter.domain.counter;

import java.net.URI;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.util.Assert;
import hu.aestallon.bigcounter.util.Pair;

public class CounterService {

  private static final Logger log = LoggerFactory.getLogger(CounterService.class);

  public static final String SCHEMA = "counter";
  public static final String ACTIVE_COUNTERS_MAP = "counters";

  private final ObjectApi objectApi;
  private final CollectionApi collectionApi;

  public CounterService(ObjectApi objectApi, CollectionApi collectionApi) {
    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
  }

  @TransactionalStorage
  public void increment(final URI counterUri) {
    Assert.notNull(counterUri, "counter URI must not be null!");
    log.debug("Incrementing Counter [ uri: {} ]", counterUri);

    final var counter = objectApi.loadLatest(counterUri);
    if (log.isTraceEnabled()) {
      log.trace("Counter before increment is: [ {} ]", counter.getObject(Counter.class));
    }

    counter.modify(Counter.class, it -> it.value(it.getValue() + 1L));
    final URI resultUri = objectApi.save(counter);
    log.debug("Incrementing Counter [ uri: {} ] yielded result URI [ {} ]", counterUri, resultUri);
  }

  @TransactionalStorage
  public URI create(final String name) {
    Assert.notNull(name, "counter name must not be null!");
    log.debug("Creating Counter [ name: {} ]", name);

    final var counter = objectApi.create(SCHEMA, new Counter()
        .name(name)
        .value(0L));
    return objectApi.save(counter);
  }

  @TransactionalStorage
  public void createAndRegister(final String name) {
    final URI counterUri = create(name);
    log.debug("Registering Counter [ name: {} ] [ uri: {} ]", name, counterUri);

    collectionApi.map(SCHEMA, ACTIVE_COUNTERS_MAP).put(name, counterUri);
  }

  public Stream<Pair<String, URI>> countersByName() {
    log.trace("Fetching all active counters.");

    return collectionApi
        .map(SCHEMA, ACTIVE_COUNTERS_MAP)
        .uris().entrySet().stream()
        .map(Pair::of);
  }

}
