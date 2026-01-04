/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides Maven plugin mojos for Metaschema code and schema generation.
 * <p>
 * This module uses the Maven API at runtime via the unnamed module, as Maven's
 * APIs have split package issues that prevent full JPMS compatibility.
 */
module gov.nist.secauto.metaschema.maven.plugin {
  // requirements
  requires java.base;

  requires transitive gov.nist.secauto.metaschema.core;
  requires transitive gov.nist.secauto.metaschema.databind;
  requires transitive gov.nist.secauto.metaschema.schemagen;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires org.apache.logging.log4j;
  requires org.apache.logging.log4j.core;

  exports gov.nist.secauto.metaschema.maven.plugin;
}
