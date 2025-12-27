/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.net.URI;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Assigns a Metapath namespace to a prefix for use in a Metapath expression in
 * a lexical qualified name.
 */
@MetaschemaAssembly(
    formalName = "Metapath Namespace Declaration",
    description = "Assigns a Metapath namespace to a prefix for use in a Metapath expression in a lexical qualified name.",
    name = "metapath-namespace",
    moduleClass = MetaschemaModelModule.class)
public class MetapathNamespace implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * The namespace URI to bind to the prefix.
   */
  @BoundFlag(
      formalName = "Metapath Namespace URI",
      description = "The namespace URI to bind to the prefix.",
      name = "uri",
      required = true,
      typeAdapter = UriAdapter.class)
  private URI _uri;

  /**
   * The prefix that is bound to the namespace.
   */
  @BoundFlag(
      formalName = "Metapath Namespace Prefix",
      description = "The prefix that is bound to the namespace.",
      name = "prefix",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _prefix;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathNamespace}
   * instance with no metadata.
   */
  public MetapathNamespace() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathNamespace}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public MetapathNamespace(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the metapath Namespace URI.
   *
   * <p>
   * The namespace URI to bind to the prefix.
   *
   * @return the uri value
   */
  @NonNull
  public URI getUri() {
    return _uri;
  }

  /**
   * Set the metapath Namespace URI.
   *
   * <p>
   * The namespace URI to bind to the prefix.
   *
   * @param value
   *          the uri value to set
   */
  public void setUri(@NonNull URI value) {
    _uri = value;
  }

  /**
   * Get the metapath Namespace Prefix.
   *
   * <p>
   * The prefix that is bound to the namespace.
   *
   * @return the prefix value
   */
  @NonNull
  public String getPrefix() {
    return _prefix;
  }

  /**
   * Set the metapath Namespace Prefix.
   *
   * <p>
   * The prefix that is bound to the namespace.
   *
   * @param value
   *          the prefix value to set
   */
  public void setPrefix(@NonNull String value) {
    _prefix = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
