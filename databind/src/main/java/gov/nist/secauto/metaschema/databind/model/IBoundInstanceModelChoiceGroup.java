/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.impl.InstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a choice group instance bound to Java field.
 */
public interface IBoundInstanceModelChoiceGroup
    extends IBoundInstanceModel<IBoundObject>, IBoundContainerModelChoiceGroup, IChoiceGroupInstance {

  /**
   * Create a new bound choice group instance.
   *
   * @param field
   *          the Java field the instance is bound to
   * @param containingDefinition
   *          the definition containing the instance
   * @return the new instance
   */
  @NonNull
  static IBoundInstanceModelChoiceGroup newInstance(
      @NonNull Field field,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    return InstanceModelChoiceGroup.newInstance(field, containingDefinition);
  }

  @Override
  default String getJsonName() {
    // always the group-as name
    return ObjectUtils.requireNonNull(getGroupAsName());
  }

  @Override
  @NonNull
  IBoundDefinitionModelAssembly getOwningDefinition();

  @Override
  default IBoundDefinitionModelAssembly getContainingDefinition() {
    return getOwningDefinition();
  }

  /**
   * Get the bound grouped model instance associated with the provided Java class.
   *
   * @param clazz
   *          the Java class which should be bound to a grouped model instance
   * @return the grouped model instance or {code null} if no instance was bound to
   *         the requested class
   */
  @Nullable
  IBoundInstanceModelGroupedNamed getGroupedModelInstance(@NonNull Class<?> clazz);

  /**
   * Get the bound grouped model instance associated with the provided XML
   * qualified name.
   *
   * @param name
   *          the XML qualified name which should be bound to a grouped model
   *          instance
   * @return the grouped model instance or {code null} if no instance was bound to
   *         the requested XML qualified name
   */
  @Nullable
  IBoundInstanceModelGroupedNamed getGroupedModelInstance(@NonNull QName name);

  /**
   * Get the bound grouped model instance associated with the provided JSON
   * discriminator value.
   *
   * @param discriminator
   *          the JSON discriminator value which should be bound to a grouped
   *          model instance
   * @return the grouped model instance or {code null} if no instance was bound to
   *         the requested JSON discriminator value
   */
  @Nullable
  IBoundInstanceModelGroupedNamed getGroupedModelInstance(@NonNull String discriminator);

  /**
   * Get the bound grouped model instance associated with the provided item.
   *
   * @param item
   *          the item which should be bound to a grouped model instance
   * @return the grouped model instance or {code null} if no instance was bound to
   *         the requested item
   */
  @Override
  @NonNull
  default IBoundInstanceModelGroupedNamed getItemInstance(Object item) {
    return ObjectUtils.requireNonNull(getGroupedModelInstance(item.getClass()));
  }

  @Override
  default IBoundObject readItem(IBoundObject parent, IItemReadHandler handler) throws IOException {
    return handler.readChoiceGroupItem(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeChoiceGroupItem(item, this);
  }

  @Override
  default IBoundObject deepCopyItem(IBoundObject item, IBoundObject parentInstance) throws BindingException {
    IBoundInstanceModelGroupedNamed itemInstance = getItemInstance(item);
    return itemInstance.deepCopyItem(item, parentInstance);
  }

  @Override
  default boolean canHandleXmlQName(@NonNull QName qname) {
    return getGroupedModelInstance(qname) != null;
  }
}
