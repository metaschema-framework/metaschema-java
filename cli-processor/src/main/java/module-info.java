/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

import gov.nist.secauto.metaschema.cli.processor.command.ICommand;

/**
 * Provides a command-line interface processing framework for Metaschema tools.
 *
 * @uses ICommand to discover CLI commands via service loader
 */
module gov.nist.secauto.metaschema.cli.processor {
  // requirements
  requires java.base;

  requires transitive gov.nist.secauto.metaschema.core;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires org.apache.commons.cli;
  requires org.jansi.core;
  requires nl.talsmasoftware.lazy4j;
  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;
  requires org.apache.logging.log4j.jul;

  exports gov.nist.secauto.metaschema.cli.processor;
  exports gov.nist.secauto.metaschema.cli.processor.command;
  exports gov.nist.secauto.metaschema.cli.processor.command.impl;
  exports gov.nist.secauto.metaschema.cli.processor.completion;

  uses ICommand;
}
