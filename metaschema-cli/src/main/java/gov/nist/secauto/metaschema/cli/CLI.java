/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli;

import gov.nist.secauto.metaschema.cli.commands.MetaschemaCommands;
import gov.nist.secauto.metaschema.cli.processor.CLIProcessor;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.command.CommandService;
import gov.nist.secauto.metaschema.core.MetaschemaJavaVersion;
import gov.nist.secauto.metaschema.core.model.MetaschemaVersion;
import gov.nist.secauto.metaschema.core.util.IVersionInfo;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.ShortClassName")
public final class CLI {
  public static void main(String[] args) {
    System.exit(runCli(args).getExitCode().getStatusCode());
  }

  @NonNull
  public static ExitStatus runCli(String... args) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

    Map<String, IVersionInfo> versions = ObjectUtils.notNull(
        new LinkedHashMap<>() {
          {
            put(CLIProcessor.COMMAND_VERSION, new MetaschemaJavaVersion());
            put("http://csrc.nist.gov/ns/oscal/metaschema/1.0", new MetaschemaVersion());
          }
        });
    CLIProcessor processor = new CLIProcessor("metaschema-cli", versions);
    MetaschemaCommands.COMMANDS.forEach(processor::addCommandHandler);

    CommandService.getInstance().getCommands().stream().forEach(command -> {
      assert command != null;
      processor.addCommandHandler(command);
    });
    return processor.process(args);
  }

  private CLI() {
    // disable construction
  }
}
