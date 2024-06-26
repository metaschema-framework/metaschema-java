/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.core.metapath.item.node;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a set of callbacks used when iterating over node items in a directed
 * graph.
 * <p>
 * The {@link AbstractNodeItemVisitor} provides an abstract implementation of
 * this visitor pattern.
 *
 * @param <CONTEXT>
 *          the type of data to pass to each visited node
 * @param <RESULT>
 *          the type of result produced by visitation
 * @see AbstractNodeItemVisitor
 */
public interface INodeItemVisitor<CONTEXT, RESULT> {
  /**
   * This callback is called when the {@link IDocumentNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitDocument(@NonNull IDocumentNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IFlagNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitFlag(@NonNull IFlagNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IFieldNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitField(@NonNull IFieldNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IAssemblyNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitAssembly(@NonNull IAssemblyNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IAssemblyInstanceGroupedNodeItem} is
   * visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitAssembly(@NonNull IAssemblyInstanceGroupedNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IModuleNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitMetaschema(@NonNull IModuleNodeItem item, CONTEXT context);
}
