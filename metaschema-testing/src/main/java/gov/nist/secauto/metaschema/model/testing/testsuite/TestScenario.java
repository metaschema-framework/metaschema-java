/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../metaschema/unit-tests.yaml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.model.testing.testsuite;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A test scenario that validates a metaschema and its content.
 */
@MetaschemaAssembly(
    formalName = "Test Scenario",
    description = "A test scenario that validates a metaschema and its content.",
    name = "test-scenario",
    moduleClass = MetaschemaTestSuiteModule.class)
public class TestScenario implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * The name of this test scenario.
   */
  @BoundFlag(
      formalName = "Name",
      description = "The name of this test scenario.",
      name = "name",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _name;

  /**
   * Defines schema generation parameters and expected results.
   */
  @BoundAssembly(
      formalName = "Generate Schema",
      description = "Defines schema generation parameters and expected results.",
      useName = "generate-schema")
  private GenerateSchema _generateSchema;

  /**
   * A content validation test case.
   */
  @BoundAssembly(
      formalName = "Validation Case",
      description = "A content validation test case.",
      useName = "validation-case",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "validation-cases", inJson = JsonGroupAsBehavior.LIST))
  private List<ValidationCase> _validationCases;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.TestScenario}
   * instance with no metadata.
   */
  public TestScenario() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.TestScenario}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public TestScenario(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the name.
   *
   * <p>
   * The name of this test scenario.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the name.
   *
   * <p>
   * The name of this test scenario.
   *
   * @param value
   *          the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
  }

  /**
   * Get the generate Schema.
   *
   * <p>
   * Defines schema generation parameters and expected results.
   *
   * @return the generate-schema value, or {@code null} if not set
   */
  @Nullable
  public GenerateSchema getGenerateSchema() {
    return _generateSchema;
  }

  /**
   * Set the generate Schema.
   *
   * <p>
   * Defines schema generation parameters and expected results.
   *
   * @param value
   *          the generate-schema value to set
   */
  public void setGenerateSchema(@Nullable GenerateSchema value) {
    _generateSchema = value;
  }

  /**
   * Get the validation Case.
   *
   * <p>
   * A content validation test case.
   *
   * @return the validation-case value
   */
  @NonNull
  public List<ValidationCase> getValidationCases() {
    if (_validationCases == null) {
      _validationCases = new LinkedList<>();
    }
    return ObjectUtils.notNull(_validationCases);
  }

  /**
   * Set the validation Case.
   *
   * <p>
   * A content validation test case.
   *
   * @param value
   *          the validation-case value to set
   */
  public void setValidationCases(@NonNull List<ValidationCase> value) {
    _validationCases = value;
  }

  /**
   * Add a new {@link ValidationCase} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addValidationCase(ValidationCase item) {
    ValidationCase value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_validationCases == null) {
      _validationCases = new LinkedList<>();
    }
    return _validationCases.add(value);
  }

  /**
   * Remove the first matching {@link ValidationCase} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeValidationCase(ValidationCase item) {
    ValidationCase value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _validationCases != null && _validationCases.remove(value);
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
