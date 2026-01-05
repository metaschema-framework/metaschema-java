/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IConstraintLoader;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.databind.model.metaschema.BindingConstraintLoader;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.test.TestMetaschema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

class IBindingContextTest {

  @Test
  void testConstraints() throws MetaschemaException, IOException {
    IConstraintLoader constraintLoader = new BindingConstraintLoader(DefaultBindingContext.instance());
    List<IConstraintSet> constraintSet = constraintLoader.load(
        ObjectUtils.notNull(Paths.get("src/test/resources/content/constraints.xml")));

    IBindingContext bindingContext = IBindingContext.builder()
        .constraintSet(constraintSet)
        .build();

    IBoundModule module = bindingContext.registerModule(TestMetaschema.class);

    IAssemblyDefinition root
        = module.getExportedAssemblyDefinitionByName(
            IEnhancedQName.of("https://csrc.nist.gov/ns/test/xml", "root").getIndexPosition());

    assertNotNull(root, "root not found");
    List<? extends IConstraint> constraints = root.getConstraints();
    assertFalse(constraints.isEmpty(), "a constraint was expected");
  }
}
