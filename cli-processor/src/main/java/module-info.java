/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

import dev.metaschema.cli.processor.command.ICommand;

/**
 * Provides a command-line interface processing framework for Metaschema tools.
 *
 * @uses ICommand to discover CLI commands via service loader
 */
module dev.metaschema.cli.processor {
  // requirements
  requires java.base;

  requires transitive dev.metaschema.core;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires org.apache.commons.cli;
  requires org.jansi.core;
  requires nl.talsmasoftware.lazy4j;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;
  requires org.apache.logging.log4j.jul;

  exports dev.metaschema.cli.processor;
  exports dev.metaschema.cli.processor.command;
  exports dev.metaschema.cli.processor.command.impl;
  exports dev.metaschema.cli.processor.completion;

  uses ICommand;
}
