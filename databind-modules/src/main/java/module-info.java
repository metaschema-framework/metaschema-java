/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides Metaschema module bindings and validation handlers including SARIF
 * output support.
 */
module dev.metaschema.databind.modules {
  // requirements
  requires java.base;

  requires transitive dev.metaschema.core;
  requires transitive dev.metaschema.databind;
  requires transitive dev.metaschema.schemagen;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires dev.harrel.jsonschema;

  exports dev.metaschema.modules.sarif;

  // open generated binding classes for reflection
  opens org.schemastore.json.sarif.x210;
}
