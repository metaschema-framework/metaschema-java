/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class KindFlagTestImpl
    extends AbstractDefinitionTest<IFlagNodeItem>
    implements IKindTest<IFlagNodeItem> {
  public KindFlagTestImpl(
      @Nullable IEnhancedQName instanceName,
      @Nullable String typeName,
      @NonNull StaticContext staticContext) {
    super("field", instanceName, typeName, staticContext);
  }

  @Override
  public Class<IFlagNodeItem> getItemClass() {
    return IFlagNodeItem.class;
  }

  @Override
  protected boolean matchesType(IFlagNodeItem item) {
    return typeName == null
        || DynamicTypeSupport.derivesFrom(item, ObjectUtils.notNull(typeName), getTestStaticContext());
  }
}
