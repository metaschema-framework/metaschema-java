/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.
package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLineAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.net.URI;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Example",
    name = "example",
    moduleClass = MetaschemaModelModule.class
)
public class Example implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Example Reference",
      name = "ref",
      typeAdapter = UriReferenceAdapter.class
  )
  private URI _ref;

  @BoundFlag(
      name = "path",
      typeAdapter = StringAdapter.class
  )
  private String _path;

  @BoundField(
      formalName = "Example Description",
      useName = "description",
      typeAdapter = MarkupLineAdapter.class
  )
  private MarkupLine _description;

  /**
   * Any explanatory or helpful information to be provided about the remarks parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks"
  )
  private Remarks _remarks;

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.Example} instance with no metadata.
   */
  public Example() {
    this(null);
  }

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.Example} instance with the specified metadata.
   *
   * @param data
   *           the metaschema data, or {@code null} if none
   */
  public Example(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the example Reference.
   *
   * @return the ref value, or {@code null} if not set
   */
  @Nullable
  public URI getRef() {
    return _ref;
  }

  /**
   * Set the example Reference.
   *
   * @param value
   *           the ref value to set
   */
  public void setRef(@Nullable URI value) {
    _ref = value;
  }

  /**
   * Get the {@code path} property.
   *
   * @return the path value, or {@code null} if not set
   */
  @Nullable
  public String getPath() {
    return _path;
  }

  /**
   * Set the {@code path} property.
   *
   * @param value
   *           the path value to set
   */
  public void setPath(@Nullable String value) {
    _path = value;
  }

  /**
   * Get the example Description.
   *
   * @return the description value, or {@code null} if not set
   */
  @Nullable
  public MarkupLine getDescription() {
    return _description;
  }

  /**
   * Set the example Description.
   *
   * @param value
   *           the description value to set
   */
  public void setDescription(@Nullable MarkupLine value) {
    _description = value;
  }

  /**
   * Get the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks parent.
   *
   * @return the remarks value, or {@code null} if not set
   */
  @Nullable
  public Remarks getRemarks() {
    return _remarks;
  }

  /**
   * Set the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks parent.
   *
   * @param value
   *           the remarks value to set
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
