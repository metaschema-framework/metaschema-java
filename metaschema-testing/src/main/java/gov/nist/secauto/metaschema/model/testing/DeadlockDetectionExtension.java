/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Optional;

/**
 * A JUnit 5 extension that detects deadlocks and dumps thread information when
 * tests fail or are aborted (e.g., due to timeout).
 * <p>
 * To use this extension, annotate your test class with:
 *
 * <pre>
 * {@literal @}ExtendWith(DeadlockDetectionExtension.class)
 * </pre>
 * <p>
 * Or register it globally via
 * {@code META-INF/services/org.junit.jupiter.api.extension.Extension}.
 */
public class DeadlockDetectionExtension implements TestWatcher {
  private static final Logger LOGGER = LogManager.getLogger(DeadlockDetectionExtension.class);

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    if (LOGGER.isErrorEnabled()) {
      LOGGER.error("Test aborted: {} - {}", context.getDisplayName(), cause.getMessage());
      dumpThreadInfo(context, "ABORTED");
    }
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    // Check if this looks like a timeout
    if (isTimeoutException(cause)) {
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error("Test timed out (possible deadlock): {}", context.getDisplayName());
      }
      dumpThreadInfo(context, "TIMEOUT");
      detectDeadlocks();
    }
  }

  private boolean isTimeoutException(Throwable cause) {
    if (cause == null) {
      return false;
    }
    String message = cause.getMessage();
    String className = cause.getClass().getName();
    return className.contains("Timeout")
        || (message != null && message.toLowerCase().contains("timed out"))
        || cause instanceof java.util.concurrent.TimeoutException
        || isTimeoutException(cause.getCause());
  }

  private void dumpThreadInfo(ExtensionContext context, String reason) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    sb.append("=".repeat(80)).append("\n");
    sb.append("THREAD DUMP - Test ").append(reason).append(": ").append(context.getDisplayName()).append("\n");
    sb.append("=".repeat(80)).append("\n\n");

    // Get all thread stack traces
    Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

    for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
      Thread thread = entry.getKey();
      StackTraceElement[] stackTrace = entry.getValue();

      sb.append("Thread: \"").append(thread.getName()).append("\"")
          .append(" (id=").append(thread.getId()).append(")")
          .append(" state=").append(thread.getState())
          .append(" daemon=").append(thread.isDaemon())
          .append("\n");

      for (StackTraceElement element : stackTrace) {
        sb.append("\tat ").append(element).append("\n");
      }
      sb.append("\n");
    }

    sb.append("=".repeat(80)).append("\n");

    if (LOGGER.isErrorEnabled()) {
      LOGGER.error(sb.toString());
    }
  }

  private void detectDeadlocks() {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    long[] deadlockedThreadIds = threadMXBean.findDeadlockedThreads();

    if (deadlockedThreadIds != null && deadlockedThreadIds.length > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("!".repeat(80)).append("\n");
      sb.append("DEADLOCK DETECTED!\n");
      sb.append("!".repeat(80)).append("\n\n");

      ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(deadlockedThreadIds, true, true);
      for (ThreadInfo threadInfo : threadInfos) {
        if (threadInfo != null) {
          sb.append("Deadlocked thread: \"").append(threadInfo.getThreadName()).append("\"\n");
          sb.append("  State: ").append(threadInfo.getThreadState()).append("\n");
          sb.append("  Blocked on: ").append(threadInfo.getLockName()).append("\n");
          sb.append("  Blocked by: ").append(threadInfo.getLockOwnerName()).append("\n");
          sb.append("  Stack trace:\n");
          for (StackTraceElement element : threadInfo.getStackTrace()) {
            sb.append("\t\tat ").append(element).append("\n");
          }
          sb.append("\n");
        }
      }

      sb.append("!".repeat(80)).append("\n");

      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(sb.toString());
      }
    }
  }

  @Override
  public void testDisabled(ExtensionContext context, Optional<String> reason) {
    // No action needed for disabled tests
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    // No action needed for successful tests
  }
}
