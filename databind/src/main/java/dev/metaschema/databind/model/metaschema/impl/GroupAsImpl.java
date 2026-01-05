/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.metaschema.binding.GroupingAs;

import edu.umd.cs.findbugs.annotations.NonNull;

class GroupAsImpl implements IGroupAs {
  @NonNull
  private final IEnhancedQName qname;
  @NonNull
  private final JsonGroupAsBehavior jsonBehavior;
  @NonNull
  private final XmlGroupAsBehavior xmlBehavior;

  public GroupAsImpl(@NonNull GroupingAs groupAs, @NonNull IModule module) {
    this.qname = ModuleUtils.parseModelName(module, ObjectUtils.requireNonNull(groupAs.getName()));
    this.jsonBehavior = ModelSupport.groupAsJsonBehavior(groupAs.getInJson());
    this.xmlBehavior = ModelSupport.groupAsXmlBehavior(groupAs.getInXml());
  }

  @Override
  public IEnhancedQName getGroupAsQName() {
    return qname;
  }

  @Override
  public JsonGroupAsBehavior getJsonGroupAsBehavior() {
    return jsonBehavior;
  }

  @Override
  public XmlGroupAsBehavior getXmlGroupAsBehavior() {
    return xmlBehavior;
  }

}
