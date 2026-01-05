/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.ModelUtil;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Default implementation of {@link IGroupAs} for bound model instances.
 * <p>
 * This class represents the group-as configuration for collection-type model
 * instances, including the qualified name and grouping behaviors for XML and
 * JSON serialization.
 */
public class DefaultGroupAs implements IGroupAs {
  @NonNull
  private final IEnhancedQName qname;
  @NonNull
  private final GroupAs annotation;

  /**
   * Constructs a new group-as configuration from the given annotation.
   *
   * @param annotation
   *          the {@link GroupAs} annotation providing the configuration
   * @param module
   *          the module used to resolve the namespace for the group name
   * @throws IllegalStateException
   *           if the annotation's name value resolves to {@code null}
   */
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public DefaultGroupAs(
      @NonNull GroupAs annotation,
      @NonNull IModule module) {
    this.annotation = annotation;
    String value = ModelUtil.resolveNoneOrDefault(annotation.name(), null);
    if (value == null) {
      throw new IllegalStateException(
          String.format("The %s#groupName value '%s' resulted in an invalid null value",
              GroupAs.class.getName(),
              annotation.name()));
    }
    this.qname = ModuleUtils.parseModelName(module, value);
  }

  @Override
  public IEnhancedQName getGroupAsQName() {
    return qname;
  }

  @Override
  public JsonGroupAsBehavior getJsonGroupAsBehavior() {
    return annotation.inJson();
  }

  @Override
  public XmlGroupAsBehavior getXmlGroupAsBehavior() {
    return annotation.inXml();
  }
}
