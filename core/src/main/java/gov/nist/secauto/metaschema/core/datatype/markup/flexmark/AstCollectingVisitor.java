/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitorBase;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class AstCollectingVisitor
    extends NodeVisitorBase {
  public static final String EOL = "\n";

  @SuppressWarnings("PMD.AvoidStringBufferField") // short lived
  private final StringBuilder strBuilder;
  private int indent; // 0;

  private AstCollectingVisitor(@NonNull StringBuilder strBuilder) {
    this.strBuilder = strBuilder;
    indent = 0;
  }

  /**
   * Generate a string representation of an AST.
   *
   * @param node
   *          the branch of the tree to visualize
   * @return the string representation of the AST.
   */
  @NonNull
  public static String asString(@NonNull Node node) {
    StringBuilder builder = new StringBuilder();
    AstCollectingVisitor visitor = new AstCollectingVisitor(builder);
    visitor.collect(node);
    return ObjectUtils.notNull(builder.toString());
  }

  private void appendIndent() {
    for (int i = 0; i < indent * 2; i++) {
      strBuilder.append(' ');
    }
  }

  private void collect(@NonNull Node node) {
    visit(node);
  }

  @Override
  protected void visit(Node node) {
    assert node != null;
    appendIndent();
    node.astString(strBuilder, true);
    strBuilder.append(EOL);
    indent++;

    try {
      super.visitChildren(node);
    } finally {
      indent--;
    }
  }
}
