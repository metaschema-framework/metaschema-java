/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides implementation classes for the Metaschema Document Model (MDM)
 * interfaces defined in {@link gov.nist.secauto.metaschema.core.mdm}.
 * <p>
 * This package contains the concrete implementations that support creating and
 * managing in-memory document structures backed by Metaschema module
 * definitions. These implementations handle the relationships between parent
 * and child nodes, manage location information, and provide factory methods for
 * creating new node instances.
 * <p>
 * Key implementation classes include:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.AbstractDMNodeItem} -
 * Base abstract implementation for all MDM node items</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.AbstractDMModelNodeItem}
 * - Base for model node items (assemblies and fields) that support flags</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.DocumentNodeItem} -
 * Implementation of document root nodes</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.DefinitionAssemblyNodeItem}
 * - Assemblies created directly from definitions (orphaned from documents)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.ChildAssemblyNodeItem} -
 * Assemblies created as children of other nodes</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.DefinitionFieldNodeItem}
 * - Fields created directly from definitions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.ChildFieldNodeItem} -
 * Fields created as children of other nodes</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.DefinitionFlagNodeItem}
 * - Flags created directly from definitions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.ChildFlagNodeItem} -
 * Flags created as children of other nodes</li>
 * </ul>
 * <p>
 * Supporting interfaces in this package include:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.IDMModelNodeItem} -
 * Interface for model nodes that support creating child flags</li>
 * <li>{@link gov.nist.secauto.metaschema.core.mdm.impl.IFeatureChildNodeItem} -
 * Interface for nodes that have a parent-child relationship</li>
 * </ul>
 * <p>
 * The implementation distinguishes between two types of node creation:
 * <ul>
 * <li><strong>Definition-based nodes</strong> - Created directly from
 * Metaschema definitions, orphaned from any document or parent node. These are
 * typically used as entry points for creating new document structures.</li>
 * <li><strong>Child nodes</strong> - Created as children of existing nodes
 * through instance references. These maintain parent-child relationships and
 * are part of a larger document structure.</li>
 * </ul>
 */

package gov.nist.secauto.metaschema.core.mdm.impl;
