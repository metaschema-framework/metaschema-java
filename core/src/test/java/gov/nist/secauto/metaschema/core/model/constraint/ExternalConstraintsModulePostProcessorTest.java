/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.model.xml.XmlMetaConstraintLoader;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.namespace.QName;

class ExternalConstraintsModulePostProcessorTest {

  @Test
  void test() throws MetaschemaException, IOException {

    List<IConstraintSet> constraints
        = new XmlMetaConstraintLoader().load(ObjectUtils.notNull(
            Paths.get("src/test/resources/content/issue184-constraints.xml")));

    IModule module
        = new ModuleLoader(CollectionUtil.singletonList(new ExternalConstraintsModulePostProcessor(constraints)))
            .load(ObjectUtils.notNull(
                Paths.get("src/test/resources/content/issue184-metaschema.xml")));

    IAssemblyDefinition definition = ObjectUtils.requireNonNull(module.getAssemblyDefinitionByName(
        new QName("http://csrc.nist.gov/ns/test/metaschema/constraint-targeting-test", "a")));

    assertEquals(1, definition.getConstraints().size());
  }

}
