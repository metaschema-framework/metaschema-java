/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IGroupAs;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.GroupingAs;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

class GroupAsImpl implements IGroupAs {
  @NonNull
  private final QName qname;
  @NonNull
  private final JsonGroupAsBehavior jsonBehavior;
  @NonNull
  private final XmlGroupAsBehavior xmlBehavior;

  public GroupAsImpl(@NonNull GroupingAs groupAs, @NonNull IModule module) {
    this.qname = module.toModelQName(ObjectUtils.requireNonNull(groupAs.getName()));
    this.jsonBehavior = ModelSupport.groupAsJsonBehavior(groupAs.getInJson());
    this.xmlBehavior = ModelSupport.groupAsXmlBehavior(groupAs.getInXml());
  }

  @Override
  public QName getGroupAsQName() {
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
