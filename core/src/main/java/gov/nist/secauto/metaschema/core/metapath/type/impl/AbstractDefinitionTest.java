/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractDefinitionTest<T extends IDefinitionNodeItem<?, ?>> implements IKindTest<T> {
  @Nullable
  protected final IEnhancedQName instanceName;
  @Nullable
  protected final String typeName;
  @NonNull
  private final String signature;
  @NonNull
  private final StaticContext testStaticContext;

  /**
   * Construct a new definition-based kind test.
   *
   * @param testName
   *          the name of the test
   * @param itemName
   *          the name of the node item to test
   * @param typeName
   *          the name of the definition or atomic type to test
   * @param staticContext
   *          the static context in which the test was declared
   */
  protected AbstractDefinitionTest(
      @NonNull String testName,
      @Nullable IEnhancedQName itemName,
      @Nullable String typeName,
      @NonNull StaticContext staticContext) {
    this.instanceName = itemName;
    this.typeName = typeName;
    this.testStaticContext = staticContext;

    StringBuilder signatureBuilder = new StringBuilder()
        .append(testName)
        .append('(');

    if (itemName != null && typeName != null) {
      signatureBuilder
          .append(itemName.toQName())
          .append(',')
          .append(typeName);
    } else if (itemName != null) {
      signatureBuilder
          .append(itemName.toQName());
    } else if (typeName != null) {
      signatureBuilder
          .append("*,")
          .append(typeName);
    }

    this.signature = ObjectUtils.notNull(signatureBuilder
        .append(')')
        .toString());
  }

  @NonNull
  protected StaticContext getTestStaticContext() {
    return testStaticContext;
  }

  @Override
  public String toSignature() {
    return signature;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isInstance(IItem item) {
    return item != null
        && IKindTest.super.isInstance(item)
        && matches((T) item);
  }

  protected boolean matches(@NonNull T item) {
    return matchesInstance(item) && matchesType(item);
  }

  protected boolean matchesInstance(@NonNull T item) {
    return instanceName == null || ObjectUtils.notNull(instanceName).equals(item.getQName());
  }

  protected abstract boolean matchesType(@NonNull T item);
}
