/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.schemagen.ModuleIndex.DefinitionEntry;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IXmlComplexType extends IXmlType {
  @NonNull
  IDefinition getDefinition();

  @Override
  default boolean isReferenced(XmlGenerationState state) {
    DefinitionEntry entry = state.getMetaschemaIndex().getEntry(getDefinition());
    return entry.isReferenced();
  }

  @Override
  default boolean isGeneratedType(XmlGenerationState state) {
    // these types are generated
    return true;
  }
}
