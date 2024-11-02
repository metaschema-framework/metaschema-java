/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import static org.assertj.core.api.Assertions.assertThat;

import gov.nist.secauto.metaschema.cli.CLI;

import org.junit.jupiter.api.Test;

import nl.altindag.log.LogCaptor;

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
