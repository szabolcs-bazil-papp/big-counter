package hu.aestallon.bigcounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class BigCounterApplication {

  private static final Logger log = LoggerFactory.getLogger(BigCounterApplication.class);

  private enum Arg {
    HELP("--help", "-h", ""),
    COMMAND("--command", "-c", "command"),
    EXECUTOR("--executor", "-e", "executor"),
    SINGLE_VERSION("--single-version", "-sv", "singleversion"),
    APP_NAME("--name", "-n", "");

    private final String longForm;
    private final String shortForm;
    private final String profile;

    Arg(String longForm, String shortForm, String profile) {
      this.longForm = longForm;
      this.shortForm = shortForm;
      this.profile = profile;
    }

    private static Arg parse(String s) {
      return Arrays.stream(Arg.values())
          .filter(it -> s.equals(it.longForm) || s.equals(it.shortForm))
          .findFirst()
          .orElseThrow();
    }
  }

  private static final class RunConfiguration {
    private boolean showHelp;
    private String appName = "big-counter-" + UUID.randomUUID();
    private final Set<Arg> args = new HashSet<>();

    private boolean isModeSelected() {
      return args.contains(Arg.COMMAND) || args.contains(Arg.EXECUTOR);
    }
  }

  private static RunConfiguration parseArgs(String[] args) {
    final var ret = new RunConfiguration();
    if (args == null || args.length == 0) {
      ret.showHelp = true;
      return ret;
    }

    boolean expectingAppName = false;
    for (var s : args) {
      try {
        if (expectingAppName) {
          ret.appName = s;
          expectingAppName = false;
        } else {
          Arg arg = Arg.parse(s);
          if (Arg.HELP == arg) {
            ret.showHelp = true;
            return ret;
          }

          if (Arg.APP_NAME == arg) {
            expectingAppName = true;
          } else {
            ret.args.add(arg);
          }
        }

      } catch (Exception e) {
        log.error("{} is not a valid argument!", s);
        ret.showHelp = true;
        return ret;
      }
    }
    return ret;
  }

  private static void showHelp() {
    try (var in = BigCounterApplication.class.getResourceAsStream("/HELP.txt")) {
      if (in == null) {
        log.error("Could not acquire HELP resource file!");
        return;
      }

      try (var br = new BufferedReader(new InputStreamReader(in));
           var lines = br.lines()) {
        lines.forEach(System.out::println);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void main(String[] args) {
    final var config = parseArgs(args);
    if (config.showHelp || !config.isModeSelected()) {
      showHelp();
      return;
    }

    final var app = new SpringApplicationBuilder(BigCounterApplication.class)
        .web(WebApplicationType.NONE)
        .profiles(config.args.stream()
            .map(it ->it.profile)
            .filter(it -> !it.isEmpty())
            .toArray(String[]::new))
        .build();

    app.addListeners(new ApplicationPidFileWriter(config.appName + ".pid"));
    app.run(args);
  }

}
