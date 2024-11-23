/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class KindAssemblyTestImpl
    extends AbstractDefinitionTest<IAssemblyNodeItem>
    implements IKindTest<IAssemblyNodeItem> {
  public KindAssemblyTestImpl(
      @Nullable IEnhancedQName instanceName,
      @Nullable String typeName,
      @NonNull StaticContext staticContext) {
    super("assembly", instanceName, typeName, staticContext);
  }

  @Override
  public Class<IAssemblyNodeItem> getItemClass() {
    return IAssemblyNodeItem.class;
  }

  @Override
  protected boolean matchesType(IAssemblyNodeItem item) {
    return typeName == null
        || DynamicTypeSupport.derivesFrom(item, ObjectUtils.notNull(typeName), getTestStaticContext());
  }
}
