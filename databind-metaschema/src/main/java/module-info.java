/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides Metaschema module bindings and validation handlers including SARIF
 * output support.
 */
module gov.nist.secauto.metaschema.databind.modules {
  // requirements
  requires java.base;

  requires transitive gov.nist.secauto.metaschema.core;
  requires transitive gov.nist.secauto.metaschema.databind;
  requires transitive gov.nist.secauto.metaschema.schemagen;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires json.schema;

  exports gov.nist.secauto.metaschema.modules.sarif;

  // open generated binding classes for reflection
  opens org.schemastore.json.sarif.x210;
}
