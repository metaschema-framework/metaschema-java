/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedField;
import gov.nist.secauto.metaschema.databind.model.impl.DefinitionField;
import gov.nist.secauto.metaschema.databind.model.impl.InstanceModelGroupedFieldComplex;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;
import java.lang.reflect.Field;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a field model instance that is a member of a choice group
 * instance.
 */
public interface IBoundInstanceModelGroupedField
    extends IBoundInstanceModelGroupedNamed, IFieldInstanceGrouped {

  /**
   * Create a new field model instance instance that is a member of a choice group
   * instance.
   *
   * @param annotation
   *          the Java annotation the instance is bound to
   * @param container
   *          the choice group instance containing the instance
   * @return the new instance
   */
  @NonNull
  static IBoundInstanceModelGroupedField newInstance(
      @NonNull BoundGroupedField annotation,
      @NonNull IBoundInstanceModelChoiceGroup container) {
    Class<? extends IBoundObject> clazz = annotation.binding();
    IBindingContext bindingContext = container.getContainingDefinition().getBindingContext();
    IBoundDefinitionModel<?> definition = bindingContext.getBoundDefinitionForClass(clazz);

    if (!(definition instanceof DefinitionField)) {
      Field field = container.getField();
      throw new IllegalStateException(String.format(
          "The '%s' annotation, bound to '%s', field '%s' on class '%s' is not bound to a Metaschema field",
          annotation.getClass(),
          annotation.binding().getName(),
          field.toString(),
          field.getDeclaringClass().getName()));
    }
    return new InstanceModelGroupedFieldComplex(annotation, (DefinitionField) definition, container);
  }

  @Override
  IBoundDefinitionModelFieldComplex getDefinition();

  @Override
  default IBoundObject readItem(IBoundObject parent, @NonNull IItemReadHandler handler) throws IOException {
    return handler.readItemField(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeItemField(item, this);
  }
}
