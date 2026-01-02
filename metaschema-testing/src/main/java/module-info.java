/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides unit testing support for Metaschema modules and constraints.
 */
module gov.nist.secauto.metaschema.testing {
  // requirements
  requires java.base;
  requires java.management;

  requires transitive gov.nist.secauto.metaschema.core;
  requires transitive gov.nist.secauto.metaschema.databind;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires nl.talsmasoftware.lazy4j;
  requires org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires transitive org.junit.jupiter.api;

  exports gov.nist.secauto.metaschema.model.testing;
  exports gov.nist.secauto.metaschema.model.testing.testsuite;

  // open binding classes for reflection
  opens gov.nist.secauto.metaschema.model.testing.testsuite;
}
