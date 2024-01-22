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

package hu.aestallon.bigcounter.domain.command;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.data.storage.TransactionalStorage;

public class CommandService {

  private static final Logger log = LoggerFactory.getLogger(CommandService.class);

  public static final String SCHEMA = "command";
  public static final String LIST = "commands";

  private final ObjectApi objectApi;
  private final CollectionApi collectionApi;

  public CommandService(ObjectApi objectApi, CollectionApi collectionApi) {
    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
  }

  @TransactionalStorage
  public URI createIncrementCommand(final int batchSize) {
    log.debug("Creating new IncrementCommand [ batchSize: {} ]", batchSize);

    return objectApi.saveAsNew(SCHEMA, new IncrementCommand().batchSize(batchSize));
  }

  @TransactionalStorage
  public void createAndRegisterIncrementCommand(final int batchSize) {
    final URI commandUri = createIncrementCommand(batchSize);
    log.debug("Registering IncrementCommand [ uri: {} ] [ batchSize: {} ]", commandUri, batchSize);

    collectionApi.list(SCHEMA, LIST).add(commandUri);
  }

  public List<URI> getCommands() {
    log.trace("Fetching all persisted commands...");

    return collectionApi.list(SCHEMA, LIST).uris();
  }

  // load hidden here to support future polymorphism:
  public IncrementCommand loadCommand(URI commandUri) {
    log.debug("Loading command [ uri: {} ]", commandUri);

    return objectApi.loadLatest(commandUri).getObject(IncrementCommand.class);
  }

}
