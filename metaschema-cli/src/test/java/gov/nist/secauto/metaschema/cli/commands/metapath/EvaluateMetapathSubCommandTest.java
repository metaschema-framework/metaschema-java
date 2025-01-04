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

import nl.altindag.log.LogCaptor;

@Execution(value = ExecutionMode.SAME_THREAD, reason = "Log capturing needs to be single threaded")
class EvaluateMetapathSubCommandTest {

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
      CLI.runCli(args);
      assertThat(captor.getInfoLogs().contains("12"));
    }
  }
}
