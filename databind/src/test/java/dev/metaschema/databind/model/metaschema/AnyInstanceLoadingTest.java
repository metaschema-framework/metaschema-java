/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;
import dev.metaschema.databind.model.metaschema.impl.DefinitionAssemblyGlobal;

class AnyInstanceLoadingTest
    extends AbstractMetaschemaTest {

  @Test
  void testAssemblyWithAnyInstanceIsLoaded() throws MetaschemaException, IOException {
    IBindingModuleLoader loader = newBindingContext().newModuleLoader();
    loader.allowEntityResolution();

    IModule module = loader.load(ObjectUtils.notNull(
        Paths.get("src/test/resources/metaschema/any/metaschema.xml")));

    Integer rootIndex = ModuleUtils.parseModelName(module, "root").getIndexPosition();
    IAssemblyDefinition rootDef = module.getScopedAssemblyDefinitionByName(rootIndex);
    assertNotNull(rootDef, "root assembly definition should be found");

    // Access the model container to verify the any instance was loaded
    DefinitionAssemblyGlobal globalDef = assertInstanceOf(DefinitionAssemblyGlobal.class, rootDef);
    assertNotNull(globalDef.getModelContainer().getAnyInstance(),
        "any instance should not be null for assembly with <any/>");
  }
}
