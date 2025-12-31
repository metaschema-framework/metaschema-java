/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../metaschema/unit-tests.yaml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.model.testing.testsuite;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValue;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValues;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Defines schema generation parameters and expected results.
 */
@MetaschemaAssembly(
    formalName = "Generate Schema",
    description = "Defines schema generation parameters and expected results.",
    name = "generate-schema",
    moduleClass = MetaschemaTestSuiteModule.class)
public class GenerateSchema implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * The expected result of schema generation.
   */
  @BoundFlag(
      formalName = "Generation Result",
      description = "The expected result of schema generation.",
      name = "generation-result",
      defaultValue = "SUCCESS",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
          @AllowedValue(value = "SUCCESS", description = "Generation succeeded."),
          @AllowedValue(value = "FAILURE", description = "Generation resulted in failure caused by some error.") })))
  private String _generationResult;

  /**
   * The expected result of content validation.
   */
  @BoundFlag(
      formalName = "Validation Result",
      description = "The expected result of content validation.",
      name = "validation-result",
      defaultValue = "VALID",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "VALID", description = "Validation succeeded."),
              @AllowedValue(value = "INVALID",
                  description = "Validation resulted in failure caused by some content defect or error.") })))
  private String _validationResult;

  /**
   * Reference to a metaschema module to load.
   */
  @BoundAssembly(
      formalName = "Metaschema",
      description = "Reference to a metaschema module to load.",
      useName = "metaschema",
      minOccurs = 1)
  private Metaschema _metaschema;

  /**
   * A schema generation comparison test case.
   */
  @BoundAssembly(
      formalName = "Generation Case",
      description = "A schema generation comparison test case.",
      useName = "generation-case",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "generation-cases", inJson = JsonGroupAsBehavior.LIST))
  private List<GenerationCase> _generationCases;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.GenerateSchema}
   * instance with no metadata.
   */
  public GenerateSchema() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.GenerateSchema}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public GenerateSchema(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the generation Result.
   *
   * <p>
   * The expected result of schema generation.
   *
   * @return the generation-result value, or {@code null} if not set
   */
  @Nullable
  public String getGenerationResult() {
    return _generationResult;
  }

  /**
   * Set the generation Result.
   *
   * <p>
   * The expected result of schema generation.
   *
   * @param value
   *          the generation-result value to set
   */
  public void setGenerationResult(@Nullable String value) {
    _generationResult = value;
  }

  /**
   * Get the validation Result.
   *
   * <p>
   * The expected result of content validation.
   *
   * @return the validation-result value, or {@code null} if not set
   */
  @Nullable
  public String getValidationResult() {
    return _validationResult;
  }

  /**
   * Set the validation Result.
   *
   * <p>
   * The expected result of content validation.
   *
   * @param value
   *          the validation-result value to set
   */
  public void setValidationResult(@Nullable String value) {
    _validationResult = value;
  }

  /**
   * Get the metaschema.
   *
   * <p>
   * Reference to a metaschema module to load.
   *
   * @return the metaschema value
   */
  @NonNull
  public Metaschema getMetaschema() {
    return _metaschema;
  }

  /**
   * Set the metaschema.
   *
   * <p>
   * Reference to a metaschema module to load.
   *
   * @param value
   *          the metaschema value to set
   */
  public void setMetaschema(@NonNull Metaschema value) {
    _metaschema = value;
  }

  /**
   * Get the generation Case.
   *
   * <p>
   * A schema generation comparison test case.
   *
   * @return the generation-case value
   */
  @NonNull
  public List<GenerationCase> getGenerationCases() {
    if (_generationCases == null) {
      _generationCases = new LinkedList<>();
    }
    return ObjectUtils.notNull(_generationCases);
  }

  /**
   * Set the generation Case.
   *
   * <p>
   * A schema generation comparison test case.
   *
   * @param value
   *          the generation-case value to set
   */
  public void setGenerationCases(@NonNull List<GenerationCase> value) {
    _generationCases = value;
  }

  /**
   * Add a new {@link GenerationCase} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addGenerationCase(GenerationCase item) {
    GenerationCase value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_generationCases == null) {
      _generationCases = new LinkedList<>();
    }
    return _generationCases.add(value);
  }

  /**
   * Remove the first matching {@link GenerationCase} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeGenerationCase(GenerationCase item) {
    GenerationCase value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _generationCases != null && _generationCases.remove(value);
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
