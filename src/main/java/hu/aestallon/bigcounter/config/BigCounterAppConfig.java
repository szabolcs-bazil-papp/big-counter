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

package hu.aestallon.bigcounter.config;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import hu.aestallon.bigcounter.domain.command.CommandService;
import hu.aestallon.bigcounter.domain.counter.CounterService;
import hu.aestallon.bigcounter.mode.CommandMode;
import hu.aestallon.bigcounter.mode.ExecutorMode;

@Configuration
@Import({PlatformApiConfig.class})
@EnableScheduling
public class BigCounterAppConfig {

  private static final Logger log = LoggerFactory.getLogger(BigCounterAppConfig.class);

  @Bean
  ObjectStorage objectStorage(@Value("${fs.base.directory:./fs}") String fsBaseDirectory,
      ObjectDefinitionApi objectDefinitionApi) {
    return new StorageFS(new File(fsBaseDirectory), objectDefinitionApi);
  }

  @Bean(Storage.STORAGETX)
  StorageTransactionManagerFS transactionManager(ObjectStorage objectStorage) {
    if (!(objectStorage instanceof StorageFS)) {
      throw new IllegalStateException("No Storage FS implementation of ObjectStorage is present!");
    }

    return new StorageTransactionManagerFS((StorageFS) objectStorage);
  }

  @Bean
  CounterService counterService(ObjectApi objectApi, CollectionApi collectionApi) {
    return new CounterService(objectApi, collectionApi);
  }

  @Bean
  CommandService commandService(ObjectApi objectApi, CollectionApi collectionApi) {
    return new CommandService(objectApi, collectionApi);
  }

  @Bean
  @Profile("command")
  CommandMode commandMode(CommandService commandService, CounterService counterService) {
    return new CommandMode(commandService, counterService);
  }

  @Bean
  @Profile("command")
  CommandLineRunner commandModeInitializer(CounterService counterService) {
    return args -> {
      log.debug("Command mode initialisation runner starting...");
      if (counterService.countersByName().findAny().isPresent()) {
        log.info("Counters found in storage. No initialisation needs to be performed.");
        return;
      }

      log.info("Initialising storage with alphabetic counters.");
      for (int i = 'a'; i <= 'z'; i++) {
        final var name = String.valueOf((char) i);
        counterService.createAndRegister(name);
      }
      log.info("Storage initialisation complete!");
    };
  }

  @Bean
  @Profile("executor")
  ExecutorMode executorMode(CommandService commandService, CounterService counterService) {
    return new ExecutorMode(commandService, counterService);
  }

  @Bean
  @Profile("singleversion")
  Storage commandStorage(ObjectStorage objectStorage, ObjectDefinitionApi objectDefinitionApi) {
    log.debug("Initialising Storage for [ {} ] with single-version policy.", CommandService.SCHEMA);
    final var commandStorage = new Storage(
        CommandService.SCHEMA,
        objectDefinitionApi,
        objectStorage);
    commandStorage.setVersionPolicy(StorageObject.VersionPolicy.SINGLEVERSION);
    return commandStorage;
  }

  @Bean
  @Profile("singleversion")
  Storage counterStorage(ObjectStorage objectStorage, ObjectDefinitionApi objectDefinitionApi) {
    log.debug("Initialising Storage for [ {} ] with single-version policy.", CounterService.SCHEMA);
    final var counterStorage = new Storage(
        CounterService.SCHEMA,
        objectDefinitionApi,
        objectStorage);
    counterStorage.setVersionPolicy(StorageObject.VersionPolicy.SINGLEVERSION);
    return counterStorage;
  }

}
