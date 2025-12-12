/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Node item types representing structured data in the Metapath type system.
 * <p>
 * This package provides interfaces and implementations for node items, which
 * form tree-structured graphs representing both Metaschema models (module
 * definitions) and data instances conforming to those models. Node items enable
 * navigation and querying of hierarchical data using Metapath expressions.
 * <p>
 * Core node type interfaces include:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem} -
 * Base interface for all node items</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem}
 * - Document root node representing a data instance</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem}
 * - Assembly nodes containing nested structures</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem}
 * - Field nodes containing values and flags</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem}
 * - Flag nodes containing simple values</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem}
 * - Module definition nodes representing Metaschema schemas</li>
 * </ul>
 * <p>
 * Node items support:
 * <ul>
 * <li>XPath-style navigation (parent, child, ancestor, descendant
 * relationships)</li>
 * <li>Document order traversal for predictable query results</li>
 * <li>Access to typed values through
 * {@link gov.nist.secauto.metaschema.core.metapath.item.node.IAtomicValuedNodeItem}</li>
 * <li>Visitor pattern traversal via
 * {@link gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemVisitor}</li>
 * <li>Factory-based creation through
 * {@link gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory}</li>
 * </ul>
 * <p>
 * Node items integrate with Metaschema model definitions in
 * {@link gov.nist.secauto.metaschema.core.model}, providing runtime
 * representations that can be queried using Metapath expressions. Each node
 * maintains references to its definition (schema-level metadata) and its
 * position within the document tree.
 *
 * @see <a href="https://www.w3.org/TR/xpath-datamodel-31/#Node">XPath 3.1 Data
 *      Model: Nodes</a>
 * @see gov.nist.secauto.metaschema.core.model
 */

package gov.nist.secauto.metaschema.core.metapath.item.node;
