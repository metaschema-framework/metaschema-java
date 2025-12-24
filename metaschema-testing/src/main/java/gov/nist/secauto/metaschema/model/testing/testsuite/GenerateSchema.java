/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.testsuite;

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
import java.lang.Override;
import java.lang.String;
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
   * "The expected result of schema generation."
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
   * "The expected result of content validation."
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

  @BoundAssembly(
      formalName = "Metaschema",
      description = "Reference to a metaschema module to load.",
      useName = "metaschema",
      minOccurs = 1)
  private Metaschema _metaschema;

  @BoundAssembly(
      formalName = "Generation Case",
      description = "A schema generation comparison test case.",
      useName = "generation-case",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "generation-cases", inJson = JsonGroupAsBehavior.LIST))
  private List<GenerationCase> _generationCases;

  public GenerateSchema() {
    this(null);
  }

  public GenerateSchema(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public String getGenerationResult() {
    return _generationResult;
  }

  public void setGenerationResult(String value) {
    _generationResult = value;
  }

  public String getValidationResult() {
    return _validationResult;
  }

  public void setValidationResult(String value) {
    _validationResult = value;
  }

  public Metaschema getMetaschema() {
    return _metaschema;
  }

  public void setMetaschema(Metaschema value) {
    _metaschema = value;
  }

  public List<GenerationCase> getGenerationCases() {
    return _generationCases;
  }

  public void setGenerationCases(List<GenerationCase> value) {
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
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
