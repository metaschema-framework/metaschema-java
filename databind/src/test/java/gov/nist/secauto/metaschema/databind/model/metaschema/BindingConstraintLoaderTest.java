/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class BindingConstraintLoaderTest {
  private static final String NS = "http://csrc.nist.gov/ns/test/metaschema/meta-constraints";

  @Test
  void testValid() throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader loader = new BindingConstraintLoader(bindingContext);

    List<IConstraintSet> constraints = loader.load(
        Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-valid.yaml"));
    assertEquals(1, constraints.size());

    Path compileDir = Paths.get("target/generated-test-modules/meta-constraints/");
    Files.createDirectories(compileDir);

    bindingContext = IBindingContext.builder()
        .compilePath(compileDir)
        .constraintSet(constraints)
        .build();

    IBindingMetaschemaModule metaschema = bindingContext.loadMetaschema(
        Paths.get("src/test/resources/content/constraints/meta-constraints/metaschema.xml"));
    IBoundModule module = bindingContext.registerModule(metaschema);

    final IAssemblyDefinition level1
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());
    final IAssemblyDefinition level2
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level2").getIndexPosition());
    final IAssemblyDefinition level3
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level3").getIndexPosition());

    assert level1 != null;
    assert level2 != null;
    assert level3 != null;

    level1.getConstraints();
    level2.getConstraints();
    level3.getConstraints();

    assertAll(
        () -> assertNotNull(level1),
        () -> assertEquals(1, level1 == null ? 0 : level1.getLetExpressions().size(), "level 1 let"),
        () -> assertEquals(1, level1 == null ? 0 : level1.getExpectConstraints().size(), "level 1 expect"),
        () -> assertNotNull(level2),
        () -> assertEquals(1, level2 == null ? 0 : level2.getLetExpressions().size(), "level 2 let"),
        () -> assertEquals(1, level2 == null ? 0 : level2.getExpectConstraints().size(), "level 2 expect"),
        () -> assertNotNull(level3),
        () -> assertEquals(1, level3 == null ? 0 : level3.getLetExpressions().size(), "level 3 let"),
        () -> assertEquals(1, level3 == null ? 0 : level3.getExpectConstraints().size(), "level 3 expect"));
  }
}
