/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import static org.assertj.core.api.Assertions.assertThat;

import gov.nist.secauto.metaschema.cli.CLI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.OutputStream;
import java.io.PrintStream;

import nl.altindag.log.LogCaptor;

@Execution(value = ExecutionMode.SAME_THREAD, reason = "Log capturing needs to be single threaded")
class EvaluateMetapathSubCommandTest {

  /**
   * A PrintStream that discards all output, used to suppress CLI console output
   * during tests.
   */
  @SuppressWarnings("resource")
  private static final PrintStream NULL_STREAM = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {
      // discard
    }
  });

  @Test
  void test() {
    try (LogCaptor captor = LogCaptor.forRoot()) {
      String[] args
          = {
              "metapath",
              "eval",
              "-e",
              "3 + 4 + 5",
              "--show-stack-trace" };
      CLI.runCli(NULL_STREAM, args);
      assertThat(captor.getInfoLogs().contains("12"));
    }
  }
}
