/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import static org.assertj.core.api.Assertions.assertThat;

import gov.nist.secauto.metaschema.cli.CLI;

import org.junit.jupiter.api.Test;

import nl.altindag.console.ConsoleCaptor;

class EvaluateMetapathSubCommandTest {

  @Test
  void test() {
    try (ConsoleCaptor consoleCaptor = new ConsoleCaptor()) {
      String[] args
          = {
              "metapath",
              "eval",
              "-m",
              "../databind/src/test/resources/metaschema/fields_with_flags/metaschema.xml",
              "-i",
              "../databind/src/test/resources/metaschema/fields_with_flags/example.json",
              "-e",
              "3 + 4 + 5",
              "--show-stack-trace" };
      CLI.runCli(args);

      assertThat(consoleCaptor.getStandardOutput()).contains("12");
    }
  }
}
