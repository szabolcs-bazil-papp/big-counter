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

package hu.aestallon.bigcounter.mode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import hu.aestallon.bigcounter.domain.command.CommandService;
import hu.aestallon.bigcounter.domain.counter.CounterService;

public class CommandMode {

  private static final Logger log = LoggerFactory.getLogger(CommandMode.class);

  @Value("${command-mode.increment.batch-size:1000000}")
  private int incrementBatchSize;

  private final CommandService commandService;
  private final CounterService counterService;

  public CommandMode(CommandService commandService, CounterService counterService) {
    this.commandService = commandService;
    this.counterService = counterService;
  }

  @Scheduled(fixedDelayString = "${command-mode.increment.freq:30000}")
  @TransactionalStorage
  public void issueIncrementCommand() {
    log.debug("Issuing new increment command with batch size [ {} ]...", incrementBatchSize);
    if (counterService.countersByName().findAny().isEmpty()) {
      log.debug("No counters found to be persisted, returning without performing any operation!");
      return;
    }

    commandService.createAndRegisterIncrementCommand(incrementBatchSize);
    log.info("New increment command issues with batch size [ {} ]", incrementBatchSize);
  }

}
