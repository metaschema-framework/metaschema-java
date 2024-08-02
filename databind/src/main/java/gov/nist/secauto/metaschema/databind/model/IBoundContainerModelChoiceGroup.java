/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IContainerModelGrouped;

import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IBoundContainerModelChoiceGroup extends IContainerModelGrouped {

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelGroupedNamed> getModelInstances();

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelGroupedNamed> getNamedModelInstances();

  @Override
  IBoundInstanceModelGroupedNamed getNamedModelInstanceByName(QName name);

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelGroupedField> getFieldInstances();

  @Override
  @Nullable
  IBoundInstanceModelGroupedField getFieldInstanceByName(QName name);

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelGroupedAssembly> getAssemblyInstances();

  @Override
  @Nullable
  IBoundInstanceModelGroupedAssembly getAssemblyInstanceByName(QName name);
}
