/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.util.ModuleUtils;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.databind.model.IGroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.ModelUtil;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DefaultGroupAs implements IGroupAs {
  @NonNull
  private final IEnhancedQName qname;
  @NonNull
  private final GroupAs annotation;

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
