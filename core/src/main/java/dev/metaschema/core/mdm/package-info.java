/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides interfaces for the Metaschema Document Model (MDM), a simple
 * module-based data model implementation for representing Metapath node items
 * backed by Metaschema definitions.
 * <p>
 * The MDM provides a way to create in-memory document structures that conform
 * to Metaschema module definitions. These structures can be queried and
 * manipulated using Metapath expressions and serve as node items in the
 * Metapath data model.
 * <p>
 * Key interfaces in this package include:
 * <ul>
 * <li>{@link dev.metaschema.core.mdm.IDMNodeItem} - Base interface for all MDM
 * node items with location tracking support</li>
 * <li>{@link dev.metaschema.core.mdm.IDMDocumentNodeItem} - Represents a
 * document root that contains the node tree</li>
 * <li>{@link dev.metaschema.core.mdm.IDMRootAssemblyNodeItem} - Represents the
 * root assembly of a document</li>
 * <li>{@link dev.metaschema.core.mdm.IDMAssemblyNodeItem} - Represents an
 * assembly (complex object) with methods to add child nodes</li>
 * <li>{@link dev.metaschema.core.mdm.IDMFieldNodeItem} - Represents a field
 * (simple content with possible flags)</li>
 * <li>{@link dev.metaschema.core.mdm.IDMFlagNodeItem} - Represents a flag
 * (attribute-like value)</li>
 * </ul>
 * <p>
 * The MDM can be used to:
 * <ul>
 * <li>Create new document structures programmatically from Metaschema
 * definitions</li>
 * <li>Build test data for validation and processing</li>
 * <li>Construct data models for transformation and query operations</li>
 * <li>Represent detached node items that can be queried with Metapath</li>
 * </ul>
 * <p>
 * Implementation classes for these interfaces are provided in the
 * {@link dev.metaschema.core.mdm.impl} package.
 */

package dev.metaschema.core.mdm;
