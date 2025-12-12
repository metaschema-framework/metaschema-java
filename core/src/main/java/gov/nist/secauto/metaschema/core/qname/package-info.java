/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Enhanced qualified name (QName) support with efficient caching and namespace
 * management.
 * <p>
 * This package provides an optimized implementation of XML qualified names that
 * reduces memory footprint through integer-based caching and instance reuse. It
 * extends the standard {@link javax.xml.namespace.QName} functionality with
 * additional features needed for Metaschema and XPath processing.
 * <h2>Key Interfaces and Classes</h2>
 * <ul>
 * <li>{@link IEnhancedQName} - Enhanced qualified name interface with caching,
 * namespace URI access, and extended QName formatting (EQName)</li>
 * <li>{@link QNameCache} - Thread-safe cache for managing commonly reused
 * qualified names with integer index-based lookup</li>
 * <li>{@link NamespaceCache} - Thread-safe cache for namespace URIs to reduce
 * memory duplication</li>
 * <li>{@link EQNameFactory} - Factory for creating and retrieving cached
 * qualified names</li>
 * <li>{@link WellKnown} - Registry of well-known XML namespaces and their
 * conventional prefixes (xml, xs, xsi, etc.)</li>
 * </ul>
 * <h2>Caching Strategy</h2>
 * <p>
 * The caching mechanism works on two levels:
 * <ol>
 * <li><b>Namespace Cache</b> - Assigns unique integer indices to namespace
 * URIs</li>
 * <li><b>QName Cache</b> - Uses namespace indices and local names as keys to
 * cache qualified names</li>
 * </ol>
 * <p>
 * Each {@link IEnhancedQName} is assigned a unique integer index position that
 * can be used for efficient storage and lookup via
 * {@link IEnhancedQName#of(int)}. This allows qualified names to be referenced
 * by integer indices rather than full objects, reducing memory overhead in data
 * structures that reference many qualified names.
 * <h2>Extended QName Format (EQName)</h2>
 * <p>
 * The package supports the XPath 3.1 Extended QName format:
 * <ul>
 * <li><code>Q{namespace}localName</code> - For names in a namespace</li>
 * <li><code>prefix:localName</code> - When a prefix is known</li>
 * <li><code>localName</code> - For names in no namespace</li>
 * </ul>
 * <p>
 * The {@link IEnhancedQName#toEQName()} method automatically resolves prefixes
 * using well-known namespaces or falls back to the braced URI notation.
 * <h2>Thread Safety</h2>
 * <p>
 * Both {@link QNameCache} and {@link NamespaceCache} use concurrent data
 * structures ({@link java.util.concurrent.ConcurrentHashMap}) to ensure
 * thread-safe operation in multi-threaded environments. The cache instances are
 * singleton objects shared across the application.
 * <h2>Well-Known Namespaces</h2>
 * <p>
 * The {@link WellKnown} class maintains a registry of standard XML namespaces
 * including:
 * <ul>
 * <li>{@code xml} - XML namespace (http://www.w3.org/XML/1998/namespace)</li>
 * <li>{@code xs} - XML Schema (http://www.w3.org/2001/XMLSchema)</li>
 * <li>{@code xsi} - XML Schema Instance
 * (http://www.w3.org/2001/XMLSchema-instance)</li>
 * <li>Metaschema-specific namespaces</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * This package is used throughout the Metaschema framework:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.metapath} - For XPath/Metapath
 * QName literals and namespace resolution</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model} - For storing element and
 * attribute qualified names in model definitions</li>
 * <li>{@link gov.nist.secauto.metaschema.databind} - For mapping between Java
 * classes and XML/JSON element names</li>
 * <li>XML/JSON serialization components throughout the framework</li>
 * </ul>
 * <h2>Performance Considerations</h2>
 * <p>
 * The caching strategy is optimized for read-heavy workloads common in
 * Metaschema processing. The integer-based indexing allows for:
 * <ul>
 * <li>Reduced memory footprint when many references to the same QName
 * exist</li>
 * <li>Fast equality comparisons using index values</li>
 * <li>Efficient storage in data structures</li>
 * </ul>
 * <p>
 * <b>Note:</b> The global shared cache may grow large in long-running
 * processes. Consider the implications for your use case (see
 * {@link QNameCache} for details).
 *
 * @see javax.xml.namespace.QName
 * @see gov.nist.secauto.metaschema.core.metapath.StaticContext
 */

package gov.nist.secauto.metaschema.core.qname;
