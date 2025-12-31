/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.
package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.net.URI;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Property",
    name = "property",
    moduleClass = MetaschemaModelModule.class
)
public class Property implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Property Name",
      name = "name",
      required = true,
      typeAdapter = TokenAdapter.class
  )
  private String _name;

  @BoundFlag(
      formalName = "Property Namespace",
      name = "namespace",
      defaultValue = "http://csrc.nist.gov/ns/oscal/metaschema/1.0",
      typeAdapter = UriAdapter.class
  )
  private URI _namespace;

  @BoundFlag(
      formalName = "Property Value",
      name = "value",
      required = true,
      typeAdapter = StringAdapter.class
  )
  private String _value;

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.Property} instance with no metadata.
   */
  public Property() {
    this(null);
  }

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.Property} instance with the specified metadata.
   *
   * @param data
   *           the metaschema data, or {@code null} if none
   */
  public Property(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the property Name.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the property Name.
   *
   * @param value
   *           the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
  }

  /**
   * Get the property Namespace.
   *
   * @return the namespace value, or {@code null} if not set
   */
  @Nullable
  public URI getNamespace() {
    return _namespace;
  }

  /**
   * Set the property Namespace.
   *
   * @param value
   *           the namespace value to set
   */
  public void setNamespace(@Nullable URI value) {
    _namespace = value;
  }

  /**
   * Get the property Value.
   *
   * @return the value value
   */
  @NonNull
  public String getValue() {
    return _value;
  }

  /**
   * Set the property Value.
   *
   * @param value
   *           the value value to set
   */
  public void setValue(@NonNull String value) {
    _value = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
