/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A binding class for the {@code metapath-context} definition.
 */
@MetaschemaAssembly(
    name = "metapath-context",
    moduleClass = MetaschemaModelModule.class)
public class MetapathContext implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * A Metapath expression identifying the model node that the constraints will be
   * applied to.
   */
  @BoundAssembly(
      description = "A Metapath expression identifying the model node that the constraints will be applied to.",
      useName = "metapath",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "metapaths", inJson = JsonGroupAsBehavior.LIST))
  private List<MetaschemaMetapath> _metapaths;

  @BoundAssembly(
      useName = "constraints")
  private AssemblyConstraints _constraints;

  @BoundAssembly(
      useName = "context",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "contexts", inJson = JsonGroupAsBehavior.LIST))
  private List<MetapathContext> _contexts;

  /**
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathContext}
   * instance with no metadata.
   */
  public MetapathContext() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathContext}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public MetapathContext(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the {@code metapath} property.
   *
   * <p>
   * A Metapath expression identifying the model node that the constraints will be
   * applied to.
   *
   * @return the metapath value
   */
  @NonNull
  public List<MetaschemaMetapath> getMetapaths() {
    if (_metapaths == null) {
      _metapaths = new LinkedList<>();
    }
    return ObjectUtils.notNull(_metapaths);
  }

  /**
   * Set the {@code metapath} property.
   *
   * <p>
   * A Metapath expression identifying the model node that the constraints will be
   * applied to.
   *
   * @param value
   *          the metapath value to set
   */
  public void setMetapaths(@NonNull List<MetaschemaMetapath> value) {
    _metapaths = value;
  }

  /**
   * Add a new {@link MetaschemaMetapath} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addMetapath(MetaschemaMetapath item) {
    MetaschemaMetapath value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_metapaths == null) {
      _metapaths = new LinkedList<>();
    }
    return _metapaths.add(value);
  }

  /**
   * Remove the first matching {@link MetaschemaMetapath} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeMetapath(MetaschemaMetapath item) {
    MetaschemaMetapath value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _metapaths != null && _metapaths.remove(value);
  }

  /**
   * Get the {@code constraints} property.
   *
   * @return the constraints value, or {@code null} if not set
   */
  @Nullable
  public AssemblyConstraints getConstraints() {
    return _constraints;
  }

  /**
   * Set the {@code constraints} property.
   *
   * @param value
   *          the constraints value to set, or {@code null} to clear
   */
  public void setConstraints(@Nullable AssemblyConstraints value) {
    _constraints = value;
  }

  /**
   * Get the {@code context} property.
   *
   * @return the context value
   */
  @NonNull
  public List<MetapathContext> getContexts() {
    if (_contexts == null) {
      _contexts = new LinkedList<>();
    }
    return ObjectUtils.notNull(_contexts);
  }

  /**
   * Set the {@code context} property.
   *
   * @param value
   *          the context value to set
   */
  public void setContexts(@NonNull List<MetapathContext> value) {
    _contexts = value;
  }

  /**
   * Add a new {@link MetapathContext} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addContext(MetapathContext item) {
    MetapathContext value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_contexts == null) {
      _contexts = new LinkedList<>();
    }
    return _contexts.add(value);
  }

  /**
   * Remove the first matching {@link MetapathContext} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeContext(MetapathContext item) {
    MetapathContext value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _contexts != null && _contexts.remove(value);
  }

  /**
   * Get the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
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
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   *
   * @param value
   *          the remarks value to set, or {@code null} to clear
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
