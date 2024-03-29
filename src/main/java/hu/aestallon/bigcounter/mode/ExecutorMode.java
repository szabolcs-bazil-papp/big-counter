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

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.scheduling.annotation.Scheduled;
import hu.aestallon.bigcounter.domain.command.CommandService;
import hu.aestallon.bigcounter.domain.command.IncrementCommand;
import hu.aestallon.bigcounter.domain.counter.CounterService;
import hu.aestallon.bigcounter.util.Pair;

public class ExecutorMode {

  private static final Logger log = LoggerFactory.getLogger(ExecutorMode.class);

  private final AtomicInteger ctr = new AtomicInteger(0);
  private final CommandService commandService;
  private final CounterService counterService;

  public ExecutorMode(CommandService commandService, CounterService counterService) {
    this.commandService = commandService;
    this.counterService = counterService;
  }

  @Scheduled(fixedDelayString = "${executor-mode.freq:1000}")
  @TransactionalStorage
  public void execute() {
    log.debug("Looking to execute new commands...");
    final List<URI> commands = commandService.getCommands();
    log.trace(
        "Commands found are [ {} ] and pointer for this runtime currently sits at index [ {} ]",
        commands, ctr.get());

    if (commands.size() <= ctr.get()) {
      log.debug("No new command found, returning without performing any operation!");
      return;
    }

    final var commandUri = commands.get(ctr.getAndIncrement());
    final IncrementCommand incrementCommand = commandService.loadCommand(commandUri);
    log.debug("Command loaded to execute: [ {} ]", incrementCommand);

    final int batchSize = incrementCommand.getBatchSize();
    for (int i = 0; i < batchSize; i++) {
      log.trace(
          "Commencing increment transaction for operation [ {} ] in batch [ {} ]",
          i, batchSize);
      counterService.countersByName().map(Pair::b).forEach(counterService::increment);
      log.trace(
          "All counters incremented for operation [ {} ] in batch [ {} ]",
          i, batchSize);
    }
    log.info("Executed command [ uri: {} ]", commandUri);
  }
}
