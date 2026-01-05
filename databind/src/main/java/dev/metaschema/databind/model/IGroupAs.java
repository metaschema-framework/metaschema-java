/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A data object to record the group as selections.
 */
public interface IGroupAs {
  /**
   * A singleton instance representing a group-as for non-grouped (singleton)
   * items.
   */
  @NonNull
  IGroupAs SINGLETON_GROUP_AS = new IGroupAs() {
    @Override
    public IEnhancedQName getGroupAsQName() {
      return null;
    }

    @Override
    public JsonGroupAsBehavior getJsonGroupAsBehavior() {
      return JsonGroupAsBehavior.NONE;
    }

    @Override
    public XmlGroupAsBehavior getXmlGroupAsBehavior() {
      return XmlGroupAsBehavior.UNGROUPED;
    }
  };

  /**
   * Get the qualified name for the group-as, which is used for JSON/YAML key
   * naming.
   *
   * @return the qualified name, or {@code null} if this is a singleton group-as
   */
  @Nullable
  IEnhancedQName getGroupAsQName();

  /**
   * Get the JSON group-as behavior that determines how grouped items are
   * serialized in JSON/YAML formats.
   *
   * @return the JSON group-as behavior
   */
  @NonNull
  JsonGroupAsBehavior getJsonGroupAsBehavior();

  /**
   * Get the XML group-as behavior that determines how grouped items are
   * serialized in XML format.
   *
   * @return the XML group-as behavior
   */
  @NonNull
  XmlGroupAsBehavior getXmlGroupAsBehavior();
}
