/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.binding.metaschema;

import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.net.URI;

@SuppressWarnings({
    "PMD.DataClass",
    "PMD.FieldNamingConventions"
})
@MetaschemaAssembly(
    formalName = "Property",
    name = "property",
    moduleClass = MetaschemaModelModule.class)
public class Property implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Property Name",
      name = "name",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _name;

  @BoundFlag(
      formalName = "Property Namespace",
      name = "namespace",
      defaultValue = "http://csrc.nist.gov/ns/oscal/metaschema/1.0",
      typeAdapter = UriAdapter.class)
  private URI _namespace;

  @BoundFlag(
      formalName = "Property Value",
      name = "value",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _value;

  public Property() {
    this(null);
  }

  public Property(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public String getName() {
    return _name;
  }

  public void setName(String value) {
    _name = value;
  }

  public URI getNamespace() {
    return _namespace;
  }

  public void setNamespace(URI value) {
    _namespace = value;
  }

  public String getValue() {
    return _value;
  }

  public void setValue(String value) {
    _value = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
