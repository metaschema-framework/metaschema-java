/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IChoiceGroupInstance
    extends IModelInstanceAbsolute, IContainerModelGrouped {

  int DEFAULT_CHOICE_GROUP_GROUP_AS_MAX_OCCURS = -1;
  @NonNull
  String DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME = "object-type";

  /**
   * {@inheritDoc}
   *
   * @see #DEFAULT_CHOICE_GROUP_GROUP_AS_MAX_OCCURS
   */
  @Override
  default int getMaxOccurs() {
    return DEFAULT_CHOICE_GROUP_GROUP_AS_MAX_OCCURS;
  }

  /**
   * Provides the Metaschema model type of "CHOICE".
   *
   * @return the model type
   */
  @Override
  default ModelType getModelType() {
    return ModelType.CHOICE_GROUP;
  }

  /**
   * Get the JSON property to use to discriminate between JSON objects.
   *
   * @return the discriminator property
   * @see #DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME
   */
  @NonNull
  String getJsonDiscriminatorProperty();

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    return true;
  }

  /**
   * Get the effective name of the JSON key flag, if a JSON key is configured.
   * <p>
   * This name is expected to be in the same namespace as the containing model
   * element (i.e. choice group, assembly, field).
   *
   * @return the name of the JSON key flag if configured, or {@code null}
   *         otherwise
   */
  @Nullable
  String getJsonKeyFlagInstanceName();

  /**
   * Get the named model instance for the provided choice group item.
   *
   * @param item
   *          the item to get the instance for
   * @return the named model instance for the provided choice group item
   */
  @NonNull
  default INamedModelInstanceGrouped getItemInstance(@NonNull Object item) {
    throw new UnsupportedOperationException("no value");
  }

  @Override
  default MarkupMultiline getRemarks() {
    // no remarks
    return null;
  }

  @SuppressWarnings("null")
  @Override
  default String toCoordinates() {
    return String.format("%s-instance:%s:%s/%s@%d",
        getModelType().toString().toLowerCase(Locale.ROOT),
        getContainingDefinition().getContainingModule().getShortName(),
        getContainingDefinition().getName(),
        getGroupAsName(),
        hashCode());
  }
}
