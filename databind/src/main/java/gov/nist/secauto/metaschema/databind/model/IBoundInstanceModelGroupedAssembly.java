/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.impl.InstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;
import java.lang.reflect.Field;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an assembly model instance that is a member of a choice group
 * instance.
 */
public interface IBoundInstanceModelGroupedAssembly
    extends IBoundInstanceModelGroupedNamed, IAssemblyInstanceGrouped {

  /**
   * Create a new assembly model instance instance that is a member of a choice
   * group instance.
   *
   * @param annotation
   *          the Java annotation the instance is bound to
   * @param container
   *          the choice group instance containing the instance
   * @return the new instance
   */
  static IBoundInstanceModelGroupedAssembly newInstance(
      @NonNull BoundGroupedAssembly annotation,
      @NonNull IBoundInstanceModelChoiceGroup container) {
    Class<? extends IBoundObject> clazz = annotation.binding();
    IBindingContext bindingContext = container.getContainingDefinition().getBindingContext();
    IBoundDefinitionModel<?> definition = bindingContext.getBoundDefinitionForClass(clazz);
    if (definition instanceof IBoundDefinitionModelAssembly) {
      return new InstanceModelGroupedAssembly(annotation, (IBoundDefinitionModelAssembly) definition, container);
    }

    Field field = container.getField();
    throw new IllegalStateException(String.format(
        "The '%s' annotation, bound to '%s', on field '%s' on class '%s' is not bound to a Metaschema assembly",
        annotation.getClass(),
        annotation.binding().getName(),
        field.toString(),
        field.getDeclaringClass().getName()));
  }

  @Override
  IBoundDefinitionModelAssembly getDefinition();

  @Override
  default IBoundObject readItem(IBoundObject parent, @NonNull IItemReadHandler handler) throws IOException {
    return handler.readItemAssembly(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeItemAssembly(item, this);
  }
}
