/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An expression that gets the document root.
 * <p>
 * Based on the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-31/#id-path-operator">path
 * operator</a>.
 * <p>
 * This class handles the root path expression "/", which selects the document
 * root node when evaluated. The evaluation follows the XPath specification for
 * absolute paths.
 */
public class RootSlashOnlyPath
    extends AbstractPathExpression<INodeItem> {

  /**
   * Construct a new path expression.
   *
   * @param text
   *          the parsed text of the expression
   */
  public RootSlashOnlyPath(@NonNull String text) {
    super(text);
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return CollectionUtil.emptyList();
  }

  @Override
  public Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRootSlashOnlyPath(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(
      DynamicContext dynamicContext,
      ISequence<?> focus) {

    return ISequence.of(ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .map(item -> Axis.ANCESTOR_OR_SELF.execute(ObjectUtils.notNull(item))
            .findFirst()
            .orElseThrow(() -> new DynamicMetapathException(DynamicMetapathException.TREAT_DOES_NOT_MATCH_TYPE,
                "Root node not found")))
        .peek(item -> {
          if (!(item instanceof IDocumentNodeItem)) {
            throw new DynamicMetapathException(DynamicMetapathException.TREAT_DOES_NOT_MATCH_TYPE,
                "The head of the tree is not a document node.");
          }
        })));
  }
}
