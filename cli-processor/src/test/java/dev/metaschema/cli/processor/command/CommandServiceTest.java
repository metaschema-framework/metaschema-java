/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Tests for {@link CommandService}.
 */
class CommandServiceTest {

  /**
   * Tests that getCommands() is thread-safe when called concurrently.
   * <p>
   * This test verifies that concurrent access to getCommands() does not cause:
   * <ul>
   * <li>NoSuchElementException from ServiceLoader iteration</li>
   * <li>Duplicate commands in the result</li>
   * <li>Inconsistent results across calls</li>
   * </ul>
   */
  @RepeatedTest(5)
  void testGetCommandsThreadSafety() throws InterruptedException {
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);
    List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
    Set<Integer> commandCounts = ConcurrentHashMap.newKeySet();
    List<List<String>> allCommandNames = Collections.synchronizedList(new ArrayList<>());

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          // Wait for all threads to be ready
          startLatch.await();

          // Call getCommands() concurrently
          List<ICommand> commands = CommandService.getInstance().getCommands();
          assertNotNull(commands);

          commandCounts.add(commands.size());

          List<String> names = commands.stream()
              .map(ICommand::getName)
              .collect(Collectors.toList());

          allCommandNames.add(names);
        } catch (Exception e) {
          exceptions.add(e);
        } finally {
          doneLatch.countDown();
        }
      });
    }

    // Start all threads simultaneously
    startLatch.countDown();

    // Wait for all threads to complete
    assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Threads should complete within timeout");
    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
        "Executor should terminate within timeout");

    // Verify no errors occurred
    assertEquals(0, exceptions.size(),
        "No exceptions should occur during concurrent access. Exceptions: " + exceptions);

    // Verify consistent command count across all calls
    assertEquals(1, commandCounts.size(),
        "All calls should return the same number of commands, but got: " + commandCounts);

    // Verify no duplicates in any result
    for (List<String> names : allCommandNames) {
      Set<String> uniqueNames = Set.copyOf(names);
      assertEquals(names.size(), uniqueNames.size(),
          "Should have no duplicate command names, but found duplicates in: " + names);
    }
  }

  /**
   * Tests that getCommands() returns consistent results (same instances).
   */
  @Test
  void testGetCommandsReturnsCachedInstances() {
    List<ICommand> commands1 = CommandService.getInstance().getCommands();
    List<ICommand> commands2 = CommandService.getInstance().getCommands();

    assertNotNull(commands1);
    assertNotNull(commands2);
    assertEquals(commands1.size(), commands2.size());

    // Commands should be the same instances (cached)
    for (int i = 0; i < commands1.size(); i++) {
      assertTrue(commands1.get(i) == commands2.get(i),
          "Commands should be cached and return same instances");
    }
  }

  /**
   * Tests that the service discovers the shell-completion command via SPI.
   */
  @Test
  void testDiscoversSpiCommands() {
    List<ICommand> commands = CommandService.getInstance().getCommands();

    assertFalse(commands.isEmpty(), "Should discover at least one command via SPI");

    List<String> names = commands.stream()
        .map(ICommand::getName)
        .collect(Collectors.toList());

    assertTrue(names.contains("shell-completion"),
        "Should discover shell-completion command. Found: " + names);
  }
}
