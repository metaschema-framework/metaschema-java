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

package gov.nist.secauto.metaschema.model.common.metapath.item;

import gov.nist.secauto.metaschema.model.common.definition.INamedDefinition;
import gov.nist.secauto.metaschema.model.common.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.model.common.metapath.format.IPathSegment;

import org.jetbrains.annotations.NotNull;

public interface IPathItem extends IItem {
  // /**
  // * Contains the paths of all nodes (including this one). This path may be incomplete if the
  // * traversal started at a branch of the node tree, or may extend past the {@link #getNodePath()}
  // if
  // * the preceding path was provided.
  // *
  // * @return a list of path segments
  // */
  // List<IPathSegment> getPath();

  /**
   * Get the Metaschema definition associated with this node.
   * 
   * @return the definition
   */
  @NotNull
  INamedDefinition getDefinition();

  /**
   * Get the path segment for this item.
   * 
   * @return the path segment
   */
  @NotNull
  IPathSegment getPathSegment();

  // Stream<IPathSegment> getPathStream();

  // /**
  // * Contains the values of all preceding nodes and this node. This path may be incomplete if the
  // * traversal started at a branch of the node tree.
  // *
  // * @return a list of nodes
  // */
  // List<? extends IPathItem> getNodePath();

  /**
   * Get the path for this node item using the provided formatter.
   * 
   * @return the formatted path
   */
  @NotNull
  default String toPath(@NotNull IPathFormatter formatter) {
    return formatter.format(getPathSegment());
  }
}