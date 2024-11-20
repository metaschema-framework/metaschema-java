/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IGroupable extends IInstance {

  int DEFAULT_GROUP_AS_MIN_OCCURS = 0;
  int DEFAULT_GROUP_AS_MAX_OCCURS = 1;
  @NonNull
  JsonGroupAsBehavior DEFAULT_JSON_GROUP_AS_BEHAVIOR = JsonGroupAsBehavior.SINGLETON_OR_LIST;
  @NonNull
  XmlGroupAsBehavior DEFAULT_XML_GROUP_AS_BEHAVIOR = XmlGroupAsBehavior.UNGROUPED;

  /**
   * Get the minimum cardinality for this associated instance. This value must be
   * less than or equal to the maximum cardinality returned by
   * {@link #getMaxOccurs()}.
   *
   * @return {@code 0} or a positive integer value
   * @see #DEFAULT_GROUP_AS_MIN_OCCURS
   */
  int getMinOccurs();

  /**
   * Get the maximum cardinality for this associated instance. This value must be
   * greater than or equal to the minimum cardinality returned by
   * {@link #getMinOccurs()}, or {@code -1} if unbounded.
   *
   * @return a positive integer value or {@code -1} if unbounded
   * @see #DEFAULT_GROUP_AS_MAX_OCCURS
   */
  int getMaxOccurs();

  /**
   * Get the name provided for grouping. An instance in Metaschema must have a
   * group name if the instance has a cardinality greater than {@code 1}.
   *
   * @return the group-as name or {@code null} if no name is configured, such as
   *         when {@link #getMaxOccurs()} = 1
   */
  @Nullable
  default String getGroupAsName() {
    // no group-as by default
    return null;
  }

  /**
   * Get the name used for the associated element wrapping a collection of
   * elements in XML. This value is required when {@link #getXmlGroupAsBehavior()}
   * = {@link XmlGroupAsBehavior#GROUPED}. This name will be the element name
   * wrapping a collection of elements.
   * <p>
   * If this instance doesn't have a namespace defined, then the module's XML
   * namespace will be used.
   *
   * @return the groupAs QName or {@code null} if no name is configured, such as
   *         when {@link #getMaxOccurs()} = 1.
   */
  @Nullable
  default IEnhancedQName getEffectiveXmlGroupAsQName() {
    return XmlGroupAsBehavior.GROUPED.equals(getXmlGroupAsBehavior())
        ? IEnhancedQName.of(
            getContainingDefinition().getQName().getNamespace(),
            ObjectUtils.requireNonNull(getGroupAsName()))
        : null;
  }

  /**
   * Gets the configured JSON group-as strategy. A JSON group-as strategy is only
   * required when {@link #getMaxOccurs()} &gt; 1.
   * <p>
   * The default for this method is {@link JsonGroupAsBehavior#NONE}, since the
   * default behavior is to have no grouping. If {@link #getMaxOccurs()} is
   * greater than {@code 1}, then the default behavior is
   * {@code #DEFAULT_JSON_GROUP_AS_BEHAVIOR}.
   *
   * @return the JSON group-as strategy, or {@code JsonGroupAsBehavior#NONE} if
   *         {@link #getMaxOccurs()} = 1
   * @see #DEFAULT_JSON_GROUP_AS_BEHAVIOR
   */
  @NonNull
  default JsonGroupAsBehavior getJsonGroupAsBehavior() {
    return JsonGroupAsBehavior.NONE;
  }

  /**
   * Gets the configured XML group-as strategy. A XML group-as strategy is only
   * required when {@link #getMaxOccurs()} &gt; 1.
   *
   * @return the JSON group-as strategy, or {@code XmlGroupAsBehavior#UNGROUPED}
   *         if {@link #getMaxOccurs()} = 1
   * @see #DEFAULT_XML_GROUP_AS_BEHAVIOR
   */
  @NonNull
  default XmlGroupAsBehavior getXmlGroupAsBehavior() {
    return DEFAULT_XML_GROUP_AS_BEHAVIOR;
  }

  /**
   * Get the item values for the provided {@code instanceValue}. An instance may
   * be singular or many valued.
   *
   * @param instanceValue
   *          the instance
   * @return the item values or an empty collection if no item values exist
   */
  @NonNull
  default Collection<?> getItemValues(@NonNull Object instanceValue) {
    // no item values by default
    return CollectionUtil.emptyList();
  }
}
