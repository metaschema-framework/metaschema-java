/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Identifies the modules used by the databind library.
 */
module gov.nist.secauto.metaschema.databind {
  // requirements
  requires java.base;
  requires java.compiler;

  requires transitive gov.nist.secauto.metaschema.core;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires com.ctc.wstx;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.dataformat.xml;
  requires transitive com.squareup.javapoet;
  requires nl.talsmasoftware.lazy4j;
  requires transitive org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires org.apache.xmlbeans;
  requires org.yaml.snakeyaml;

  requires flexmark.util.sequence;

  exports gov.nist.secauto.metaschema.databind;
  exports gov.nist.secauto.metaschema.databind.codegen;
  exports gov.nist.secauto.metaschema.databind.codegen.config;
  // exports gov.nist.secauto.metaschema.databind.codegen.typeinfo;
  exports gov.nist.secauto.metaschema.databind.io;
  exports gov.nist.secauto.metaschema.databind.io.json;
  exports gov.nist.secauto.metaschema.databind.io.xml;
  exports gov.nist.secauto.metaschema.databind.io.yaml;
  exports gov.nist.secauto.metaschema.databind.model;
  exports gov.nist.secauto.metaschema.databind.model.info;
  exports gov.nist.secauto.metaschema.databind.model.annotations;
  exports gov.nist.secauto.metaschema.databind.model.metaschema;
  exports gov.nist.secauto.metaschema.databind.model.metaschema.binding;

  // need to allow access to the generated XMLBeans files
  opens org.apache.xmlbeans.metadata.system.metaschema.codegen;
  opens gov.nist.secauto.metaschema.databind.codegen.xmlbeans;
  opens gov.nist.secauto.metaschema.databind.codegen.xmlbeans.impl;
  // opens gov.nist.secauto.metaschema.databind.model.metaschema.binding;
}
