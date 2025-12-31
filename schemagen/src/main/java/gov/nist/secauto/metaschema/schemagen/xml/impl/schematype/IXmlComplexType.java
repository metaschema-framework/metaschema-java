/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.schemagen.ModuleIndex.DefinitionEntry;
import gov.nist.secauto.metaschema.schemagen.xml.impl.IXmlGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an XML Schema complex type derived from a Metaschema model
 * definition.
 * <p>
 * Complex types are used to represent Metaschema assemblies and fields that
 * have flags (attributes) or child elements. Unlike simple types, complex types
 * can have both element content and attributes.
 */
public interface IXmlComplexType extends IXmlType {
  /**
   * Get the Metaschema definition that this complex type is derived from.
   *
   * @return the underlying Metaschema definition
   */
  @NonNull
  IDefinition getDefinition();

  @Override
  default boolean isReferenced(IXmlGenerationState state) {
    DefinitionEntry entry = state.getMetaschemaIndex().getEntry(getDefinition());
    return entry.isReferenced();
  }

  @Override
  default boolean isGeneratedType(IXmlGenerationState state) {
    // these types are generated
    return true;
  }
}
