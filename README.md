# The Big Counter Experiment

## Introduction

This application is designed to run in miniature clusters of itself to simulate large amounts of
parallel I/O operations performed against a single ***file system storage***. Users are expected to
launch multiple instances of the application - one in command mode, and the rest in executor mode.
Each instance running in executor mode will rapidly load, increment and persist a collection of
shared counters. The collection of counters and the increment instructions are issued and overseen
by the single command mode instance.

The purpose of the application is to verify that parallel transactional attempts complete without
errors, single-version and multi-version storage objects are correctly read and updated, and no
OS-level procedures interfere with the file system storage transaction cycle.

## Installation

1. Ensure the [smartbit4all platform](https://github.com/smartbit4all/platform) is present in the
   project's **parent folder**.
2. Standing in the project's folder, build it using the gradle wrapper:
    - on Linux/MacOS:
      > `./gradlew build`
    - on Windows:
      > `.\gradlew build`
3. The ready-to-execute JAR can be found at `./build/libs/big-counter-1.0.0.jar`

---

## Execution

To run the application to display the available arguments and run-modes, issue:

```shell
java -jar big-counter-1.0.0.jar --help
```

Identical information about program arguments can be found in
the [HELP.txt](./src/main/resources/HELP.txt).

The application can be launched in either ***Command*** or ***Executor*** mode. Although it is
possible to run the application with both modes enabled, the utility of such execution is
questionable. It is advised to run one instance in ***Command*** mode by providing the `--command`
flag (shorthand `-c`) and at least one another with the `--executor` flag (shorthand `-e`).

The storage shared by all commander and executor instances can be configured to run with single
versioning policy by providing the `--single-version` flag (shorthand `-sv`). **Important:** when
running multiple instances in parallel, make sure they all run with this flag enabled (or disabled).
**Note:** This flag does not affect platform-level storage instances, it only affects how counters
and commands are stored in respect to this application.

Instances can be named by providing the `--name <custom-name>` (shorthand `-n`) argument. Upon
startup, every instance writes its PID to its `<application-name>.pid` file, to help the user to
stop the instances when needed. If the `--name` argument is omitted, the application will name
itself with a unique, pseudorandom name and create a corresponding file containing its PID. A custom
name **must be provided** if the `--name` flag is present.

## Behaviour in Command Mode

When running in command mode, the application will first initialise the file system storage for
operation by persisting a collection of `Counter` objects (if needed). While running, it will
periodically issue an _increment command_ by persisting an `IncrementCommand` object containing a
configurable batch size.

## Behaviour in Executor Mode

In this mode, the application regularly checks the `IncrementCommand` collection. When encountering
a new command, the runtime will transactionally load, increment and save every stored `Counter`,
then repeat this process as many times as prescribed by the observed command. Increments happen by
increasing the counter's value by 1, thus each executor runtime
performs `number_of_counters*command_batch_size` transactions per command.

## Example Usage

Launching a commander and an executor instance with single-version storage can be performed with the
following example commands:

```shell
java -jar big-counter-1.0.0.jar -c -n CommanderInstance -sv
java -jar big-counter-1.0.0.jar -e -n FollowerInstance -sv
```

## Customising Behaviour

All elements of the above described operation can be customised by placing
an `application.properties` file next to the JAR file, such as

- for Command mode:
    - frequency of issuing commands
    - batch size of issued commands
- for Executor mode:
    - frequency of checking for new commands
- for all modes:
    - location of the file system storage
    - log levels _(note: Turning the log level to `TRACE` may result in expensive logging calls,
      affecting performance)_

---

## Batch Scripts for Running Clusters

The repository contains a utility script for launching a cluster of one commander and multiple
executor instances ([launch-n.bat](./launch-n.bat)), and another to seamlessly stop all these
instances ([stop.bat](./stop.bat)). **The JAR file must be present in the working directory to
operate these scripts!**

Scripts are provided for the Windows operating system only.

### Launch Script Usage

```cmd
.\launch-n.bat <number-of-executors>
```

E.g.: performing `.\launch-n.bat 5` will start 1 ***Command*** runtime and 5 ***Executor***
runtimes.

All runtimes launched by the above script use a single versioning policy storage. Executor runtimes
are named sequentially. All logging is redirected to a separate `.log` file per runtime.

### Stop Script Usage

Simply execute the following:

```cmd
.\stop.bat
```

---

## Additional Considerations

Executors are not yet prepared to halt and resume execution (the runtime does not remember how many
commands it had fulfilled after it has been terminated and relaunched). Thus, it is advised to clear
the file system storage before launching the "mini cluster" to avoid inconsistent overwrites.

The nature of the I/O operations is obviously not realistic, the domain is chosen for its clarity
only.
