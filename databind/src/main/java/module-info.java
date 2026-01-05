/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Identifies the modules used by the databind library.
 */
module dev.metaschema.databind {
  // requirements
  requires java.base;
  requires java.compiler;

  requires transitive dev.metaschema.core;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires com.ctc.wstx;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.dataformat.xml;
  requires com.squareup.javapoet;
  requires nl.talsmasoftware.lazy4j;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires org.yaml.snakeyaml;

  requires flexmark.util.sequence;

  exports dev.metaschema.databind;
  exports dev.metaschema.databind.codegen;
  exports dev.metaschema.databind.codegen.config;
  exports dev.metaschema.databind.config.binding;
  // exports dev.metaschema.databind.codegen.typeinfo;
  exports dev.metaschema.databind.io;
  exports dev.metaschema.databind.io.json;
  exports dev.metaschema.databind.io.xml;
  exports dev.metaschema.databind.io.yaml;
  exports dev.metaschema.databind.model;
  exports dev.metaschema.databind.model.info;
  exports dev.metaschema.databind.model.annotations;
  exports dev.metaschema.databind.model.metaschema;
  exports dev.metaschema.databind.model.metaschema.binding;

  // open binding classes for reflection
  opens dev.metaschema.databind.config.binding;
}
