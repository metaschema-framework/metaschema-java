/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.xml.XmlConstraintLoader;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.test.TestMetaschema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

class DefaultBindingContextTest {

  @Test
  void testConstraints() throws MetaschemaException, IOException {
    IConstraintLoader constraintLoader = new XmlConstraintLoader();
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
