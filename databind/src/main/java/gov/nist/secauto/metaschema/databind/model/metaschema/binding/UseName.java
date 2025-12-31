/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.NonNegativeIntegerAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import java.math.BigInteger;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Allows the name of the definition to be overridden.
 */
@MetaschemaField(
    formalName = "Use Name",
    description = "Allows the name of the definition to be overridden.",
    name = "use-name",
    moduleClass = MetaschemaModelModule.class)
public class UseName implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * Used for binary formats instead of the textual name.
   */
  @BoundFlag(
      formalName = "Numeric Index",
      description = "Used for binary formats instead of the textual name.",
      name = "index",
      typeAdapter = NonNegativeIntegerAdapter.class)
  private BigInteger _index;

  @BoundFieldValue(
      valueKeyName = "name",
      typeAdapter = TokenAdapter.class)
  private String _name;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.UseName}
   * instance with no metadata.
   */
  public UseName() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.UseName}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public UseName(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the numeric Index.
   *
   * <p>
   * Used for binary formats instead of the textual name.
   *
   * @return the index value, or {@code null} if not set
   */
  @Nullable
  public BigInteger getIndex() {
    return _index;
  }

  /**
   * Set the numeric Index.
   *
   * <p>
   * Used for binary formats instead of the textual name.
   *
   * @param value
   *          the index value to set, or {@code null} to clear
   */
  public void setIndex(@Nullable BigInteger value) {
    _index = value;
  }

  @Nullable
  public String getName() {
    return _name;
  }

  public void setName(@Nullable String value) {
    _name = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
