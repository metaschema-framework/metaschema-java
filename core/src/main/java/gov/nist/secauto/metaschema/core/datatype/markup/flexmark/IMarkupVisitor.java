/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.vladsch.flexmark.util.ast.Document;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("THROWS_METHOD_THROWS_CLAUSE_THROWABLE")
public interface IMarkupVisitor<T, E extends Throwable> {
  /**
   * A visitor callback used to visit a markdown syntax tree.
   *
   * @param document
   *          the markdown syntax tree
   * @param writer
   *          a markup writer used to generate markup output
   * @throws E
   *           the visitor exception Java type
   */
  void visitDocument(@NonNull Document document, @NonNull IMarkupWriter<T, E> writer) throws E;
}
