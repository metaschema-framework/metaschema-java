/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

import dev.metaschema.cli.processor.command.ICommand;

/**
 * Provides a command-line interface for Metaschema operations including schema
 * generation, content validation, and Metapath evaluation.
 *
 * @provides ICommand for CLI command discovery
 */
module dev.metaschema.cli {
  // requirements
  requires java.base;

  requires transitive dev.metaschema.core;
  requires transitive dev.metaschema.databind;
  requires transitive dev.metaschema.schemagen;
  requires transitive dev.metaschema.cli.processor;
  requires dev.metaschema.databind.modules;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires nl.talsmasoftware.lazy4j;
  requires org.apache.commons.cli;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires org.json;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;

  exports dev.metaschema.cli;
  exports dev.metaschema.cli.commands;
  exports dev.metaschema.cli.commands.metapath;
  exports dev.metaschema.cli.util;

  provides ICommand with
      dev.metaschema.cli.commands.ValidateModuleCommand,
      dev.metaschema.cli.commands.GenerateSchemaCommand,
      dev.metaschema.cli.commands.GenerateDiagramCommand,
      dev.metaschema.cli.commands.ListAllowedValuesCommand,
      dev.metaschema.cli.commands.ValidateContentUsingModuleCommand,
      dev.metaschema.cli.commands.ConvertContentUsingModuleCommand,
      dev.metaschema.cli.commands.metapath.MetapathCommand;
}
