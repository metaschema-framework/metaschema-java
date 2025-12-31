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
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValue;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValues;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import java.net.URI;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A content validation test case.
 */
@MetaschemaAssembly(
    formalName = "Validation Case",
    description = "A content validation test case.",
    name = "validation-case",
    moduleClass = MetaschemaTestSuiteModule.class)
public class ValidationCase implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * The format of the source content.
   */
  @BoundFlag(
      formalName = "Source Format",
      description = "The format of the source content.",
      name = "source-format",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "XML", description = "Content is XML."),
              @AllowedValue(value = "JSON", description = "Content is JSON."),
              @AllowedValue(value = "YAML", description = "Content is YAML.") })))
  private String _sourceFormat;

  /**
   * A URI reference to the content file location.
   */
  @BoundFlag(
      formalName = "Location",
      description = "A URI reference to the content file location.",
      name = "location",
      required = true,
      typeAdapter = UriReferenceAdapter.class)
  private URI _location;

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
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.ValidationCase}
   * instance with no metadata.
   */
  public ValidationCase() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.ValidationCase}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public ValidationCase(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the source Format.
   *
   * <p>
   * The format of the source content.
   *
   * @return the source-format value, or {@code null} if not set
   */
  @Nullable
  public String getSourceFormat() {
    return _sourceFormat;
  }

  /**
   * Set the source Format.
   *
   * <p>
   * The format of the source content.
   *
   * @param value
   *          the source-format value to set
   */
  public void setSourceFormat(@Nullable String value) {
    _sourceFormat = value;
  }

  /**
   * Get the location.
   *
   * <p>
   * A URI reference to the content file location.
   *
   * @return the location value
   */
  @NonNull
  public URI getLocation() {
    return _location;
  }

  /**
   * Set the location.
   *
   * <p>
   * A URI reference to the content file location.
   *
   * @param value
   *          the location value to set
   */
  public void setLocation(@NonNull URI value) {
    _location = value;
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

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
