/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Modules used by the Metaschema schema generator.
 */
module dev.metaschema.schemagen {
  // requirements
  requires java.base;
  requires java.xml;

  requires transitive dev.metaschema.core;
  requires transitive dev.metaschema.databind;

  requires com.ctc.wstx;
  requires com.github.spotbugs.annotations;
  requires nl.talsmasoftware.lazy4j;
  requires transitive org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires org.codehaus.stax2;
  requires org.eclipse.jdt.annotation;

  exports dev.metaschema.schemagen;
  exports dev.metaschema.schemagen.json;
  exports dev.metaschema.schemagen.xml;
}
