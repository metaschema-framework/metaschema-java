/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages the exit code of a command line process.
 * <p>
 * Logging solution based on
 * https://stackoverflow.com/questions/24205093/how-to-create-a-custom-appender-in-log4j2.
 */
@Execution(value = ExecutionMode.SAME_THREAD, reason = "Log capturing needs to be single threaded")
class ExitCodeTest {
  private static MockedAppender mockedAppender;
  private static Logger logger;

  @BeforeEach
  public void setup() {
    mockedAppender.events.clear();
  }

  @BeforeAll
  public static void setupClass() {
    mockedAppender = new MockedAppender();
    logger = (Logger) LogManager.getLogger(AbstractExitStatus.class);
    logger.addAppender(mockedAppender);
    // logger.setLevel(Level.INFO);
    mockedAppender.start();
  }

  @Test
  void testExitMessage() {
    Throwable ex = new IllegalStateException("a message");
    ExitStatus exitStatus = ExitCode.FAIL.exit().withThrowable(ex);
    exitStatus.generateMessage(false);

    List<LogEvent> events = mockedAppender.getEvents();
    assertAll(
        () -> assertEquals(1, events.size()),
        () -> assertEquals("a message", events.get(0).getMessage().getFormattedMessage()));
  }

  @Test
  void testExitThrown() {
    Throwable ex = new IllegalStateException("a message");
    ExitStatus exitStatus = ExitCode.FAIL.exit().withThrowable(ex);
    exitStatus.generateMessage(true);

    List<LogEvent> events = mockedAppender.getEvents();
    assertAll(
        () -> assertEquals(1, events.size()),
        () -> assertEquals(ex, events.get(0).getThrown()),
        () -> assertEquals("a message", events.get(0).getMessage().getFormattedMessage()));
  }

  private static class MockedAppender
      extends AbstractAppender {

    private final List<LogEvent> events = new LinkedList<>();

    protected MockedAppender() {
      super("MockedAppender", null, null, false, null);
    }

    public List<LogEvent> getEvents() {
      return events;
    }

    @Override
    public void append(LogEvent event) {
      synchronized (this) {
        events.add(event.toImmutable());
      }
    }
  }
}
