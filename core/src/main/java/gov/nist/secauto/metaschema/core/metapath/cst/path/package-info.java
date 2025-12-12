/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides concrete syntax tree (CST) node implementations for Metapath path
 * navigation expressions.
 * <p>
 * This package implements path expressions as defined by
 * <a href="https://www.w3.org/TR/xpath-31/#id-path-expressions">XPath 3.1 path
 * expressions</a> and <a href="https://www.w3.org/TR/xpath-31/#axes">XPath 3.1
 * axes</a>. Path expressions enable navigation through Metaschema document
 * structures using axes, steps, and predicates.
 *
 * <h2>Key Classes and Interfaces</h2>
 *
 * <h3>Path Expression Nodes</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.AbstractPathExpression}
 * - Base class for all path expressions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.RootSlashPath}
 * - Absolute path starting with {@code /}</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.RootSlashOnlyPath}
 * - Root-only path expression {@code /}</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.RootDoubleSlashPath}
 * - Absolute descendant path starting with {@code //}</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.RelativeSlashPath}
 * - Relative path using {@code /} separator</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.RelativeDoubleSlashPath}
 * - Relative descendant path using {@code //} separator</li>
 * </ul>
 *
 * <h3>Step Expressions</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.Step} -
 * Individual step in a path expression combining axis and node test</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.Axis} -
 * Enumeration of XPath axes (child, parent, ancestor, descendant, etc.)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.ModelInstanceStep}
 * - Step for navigating model instances (assembly/field children)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.FlagStep} -
 * Step for navigating flag children</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.ContextItem} -
 * The context item expression {@code .}</li>
 * </ul>
 *
 * <h3>Node Tests</h3>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.INodeTestExpression}
 * - Interface for node test expressions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.NameNodeTest} -
 * Tests nodes by qualified name</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.KindNodeTest} -
 * Tests nodes by kind (element, attribute, etc.)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.WildcardNodeTest}
 * - Wildcard node test ({@code *}, {@code prefix:*}, {@code *:local})</li>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath.cst.path.IWildcardMatcher}
 * - Interface for wildcard matching strategies</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>
 * // Absolute path: /catalog/group/control
 * // Descendant search: //control[@id='ac-1']
 * // Relative path: parent/child
 * // Context item: .
 * // Wildcard: control/*
 * </pre>
 *
 * @see gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem
 * @see gov.nist.secauto.metaschema.core.metapath.cst
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;
