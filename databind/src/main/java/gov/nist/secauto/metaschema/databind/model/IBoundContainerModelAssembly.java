/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IContainerModelAssembly;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IBoundContainerModelAssembly extends IContainerModelAssembly {
  @Override
  @NonNull
  IBoundDefinitionModelAssembly getOwningDefinition();

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModel<?>> getModelInstances();

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelNamed<?>> getNamedModelInstances();

  @Override
  @Nullable
  IBoundInstanceModelNamed<?> getNamedModelInstanceByName(QName name);

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelField<?>> getFieldInstances();

  @Override
  @Nullable
  IBoundInstanceModelField<?> getFieldInstanceByName(QName name);

  @Override
  @NonNull
  Collection<? extends IBoundInstanceModelAssembly> getAssemblyInstances();

  @Override
  @Nullable
  IBoundInstanceModelAssembly getAssemblyInstanceByName(QName name);

  @Override
  IBoundInstanceModelChoiceGroup getChoiceGroupInstanceByName(String name);

  @Override
  Map<String, ? extends IBoundInstanceModelChoiceGroup> getChoiceGroupInstances();
}
