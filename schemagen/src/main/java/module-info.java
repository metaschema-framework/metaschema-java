/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

module gov.nist.secauto.metaschema.schemagen {
  // requirements
  requires java.base;
  requires java.xml;

  requires transitive gov.nist.secauto.metaschema.core;
  requires transitive gov.nist.secauto.metaschema.databind;

  requires com.ctc.wstx;
  requires com.github.spotbugs.annotations;
  requires nl.talsmasoftware.lazy4j;
  requires transitive org.apache.commons.lang3;
  requires org.apache.logging.log4j;
  requires org.jdom2;

  requires Saxon.HE;

  exports gov.nist.secauto.metaschema.schemagen;
  exports gov.nist.secauto.metaschema.schemagen.json;
  exports gov.nist.secauto.metaschema.schemagen.xml;
}
