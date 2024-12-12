/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#dt-expanded-qname">expanded QName
 * name test</a>.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class KindNodeTest
    implements INodeTestExpression {

  @NonNull
  private final IItemType type;

  /**
   * Construct a new expanded QName-based literal expression.
   *
   * @param name
   *          the literal value
   */
  public KindNodeTest(@NonNull IItemType type) {
    this.type = type;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitKindNodeTest(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .filter(type::isInstance)));
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[kind=%s]", getClass().getName(), type.toSignature());
  }
}
