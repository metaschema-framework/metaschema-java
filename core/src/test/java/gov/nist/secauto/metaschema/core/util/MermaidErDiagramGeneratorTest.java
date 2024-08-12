/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

class MermaidErDiagramGeneratorTest {

  @Test
  void testErDiagram() throws IOException, MetaschemaException {
    MermaidErDiagramGenerator visitor = new MermaidErDiagramGenerator();

    IModule module = new ModuleLoader()
        .load(Paths.get("metaschema/schema/metaschema/metaschema-module-metaschema.xml"));

    try (PrintWriter writer = new PrintWriter(System.out)) {
      visitor.generate(module, writer);
    }
  }
}
