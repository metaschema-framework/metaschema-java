/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

class ModuleLoaderTest {

  @Test
  void testUrl() throws MetaschemaException, IOException { // NOPMD - intentional
    ModuleLoader loader = new ModuleLoader();
    loader.allowEntityResolution();
    URI moduleUri = ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.0.0/src/metaschema/oscal_complete_metaschema.xml"));
    IXmlMetaschemaModule module = loader.load(moduleUri);

    IXmlMetaschemaModule oscalCatalogModule = module.getImportedModuleByShortName("oscal-catalog");
    assertNotNull(oscalCatalogModule, "catalog metaschema not found");
    IXmlMetaschemaModule metadataModule = oscalCatalogModule.getImportedModuleByShortName("oscal-metadata");
    assertNotNull(metadataModule, "metadata metaschema not found");
    IFlagDefinition flag
        = metadataModule.getScopedFlagDefinitionByName(IEnhancedQName.of("location-type"));
    assertNotNull(flag, "flag not found");
    List<? extends IConstraint> constraints = flag.getConstraints();
    assertFalse(constraints.isEmpty(), "a constraint was expected");
  }

  @Test
  void testFile() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    URI moduleUri = ObjectUtils.notNull(
        Paths.get("src/test/resources/content/custom-entity-metaschema.xml").toUri());
    IXmlMetaschemaModule module = loader.load(moduleUri);
    assertFalse(module.getExportedRootAssemblyDefinitions().isEmpty(), "no roots found");
  }

  @Test
  void testConstraints() throws MetaschemaException, IOException { // NOPMD - intentional
    IConstraintLoader constraintLoader = new XmlConstraintLoader();
    List<IConstraintSet> constraintSet = constraintLoader.load(
        ObjectUtils.notNull(Paths.get("src/test/resources/content/oscal-constraints.xml")));

    ExternalConstraintsModulePostProcessor postProcessor
        = new ExternalConstraintsModulePostProcessor(constraintSet);
    ModuleLoader loader = new ModuleLoader(CollectionUtil.singletonList(postProcessor));
    loader.allowEntityResolution();
    URI moduleUri = ObjectUtils.notNull(URI.create(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.0.0/src/metaschema/oscal_complete_metaschema.xml"));
    IXmlMetaschemaModule module = loader.load(moduleUri);
    IAssemblyDefinition catalog
        = module.getExportedAssemblyDefinitionByName(
            IEnhancedQName.of("http://csrc.nist.gov/ns/oscal/1.0", "catalog").getIndexPosition());

    assertNotNull(catalog, "catalog not found");
    List<? extends IConstraint> constraints = catalog.getConstraints();
    assertFalse(constraints.isEmpty(), "a constraint was expected");
  }

  @Test
  void testLoadMetaschemaWithExternalEntity() throws MetaschemaException, IOException {
    ModuleLoader loader = new ModuleLoader();
    loader.allowEntityResolution();
    IXmlMetaschemaModule module
        = loader.load(ObjectUtils.notNull(Paths.get("src/test/resources/content/custom-entity-metaschema.xml")));

    IAssemblyDefinition root = module.getExportedRootAssemblyDefinitionByName(
        IEnhancedQName.of("http://csrc.nist.gov/ns/test/metaschema/entity", "root").getIndexPosition());
    assert root != null;
    List<? extends IAllowedValuesConstraint> allowedValues = root.getAllowedValuesConstraints();

    assertAll(
        () -> assertEquals(1, allowedValues.size(), "Expecting a single constraint."),
        () -> assertEquals(1, allowedValues.get(0).getAllowedValues().size(),
            "Expecting a single allowed value. Entity reference not parsed."));
  }
}
