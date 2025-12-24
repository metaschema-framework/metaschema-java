/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.testsuite;

import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValue;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValues;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import java.lang.Override;
import java.lang.String;
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
   * "The format of the source content."
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
   * "A URI reference to the content file location."
   */
  @BoundFlag(
      formalName = "Location",
      description = "A URI reference to the content file location.",
      name = "location",
      required = true,
      typeAdapter = UriReferenceAdapter.class)
  private URI _location;

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

  public ValidationCase() {
    this(null);
  }

  public ValidationCase(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public String getSourceFormat() {
    return _sourceFormat;
  }

  public void setSourceFormat(String value) {
    _sourceFormat = value;
  }

  public URI getLocation() {
    return _location;
  }

  public void setLocation(URI value) {
    _location = value;
  }

  public String getValidationResult() {
    return _validationResult;
  }

  public void setValidationResult(String value) {
    _validationResult = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
