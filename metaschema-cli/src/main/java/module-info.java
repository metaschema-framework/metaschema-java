/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

import gov.nist.secauto.metaschema.cli.processor.command.ICommand;

/**
 * Provides a command-line interface for Metaschema operations including schema
 * generation, content validation, and Metapath evaluation.
 *
 * @provides ICommand for CLI command discovery
 */
module gov.nist.secauto.metaschema.cli {
  // requirements
  requires java.base;

  requires transitive gov.nist.secauto.metaschema.core;
  requires transitive gov.nist.secauto.metaschema.databind;
  requires transitive gov.nist.secauto.metaschema.schemagen;
  requires transitive gov.nist.secauto.metaschema.cli.processor;
  requires gov.nist.secauto.metaschema.databind.modules;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires nl.talsmasoftware.lazy4j;
  requires org.apache.commons.cli;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;

  exports gov.nist.secauto.metaschema.cli;
  exports gov.nist.secauto.metaschema.cli.commands;
  exports gov.nist.secauto.metaschema.cli.commands.metapath;
  exports gov.nist.secauto.metaschema.cli.util;

  provides ICommand with
      gov.nist.secauto.metaschema.cli.commands.ValidateModuleCommand,
      gov.nist.secauto.metaschema.cli.commands.GenerateSchemaCommand,
      gov.nist.secauto.metaschema.cli.commands.GenerateDiagramCommand,
      gov.nist.secauto.metaschema.cli.commands.ValidateContentUsingModuleCommand,
      gov.nist.secauto.metaschema.cli.commands.ConvertContentUsingModuleCommand,
      gov.nist.secauto.metaschema.cli.commands.metapath.MetapathCommand;
}
