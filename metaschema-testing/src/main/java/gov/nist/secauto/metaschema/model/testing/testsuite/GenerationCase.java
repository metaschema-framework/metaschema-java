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
 * A schema generation comparison test case.
 */
@MetaschemaAssembly(
    formalName = "Generation Case",
    description = "A schema generation comparison test case.",
    name = "generation-case",
    moduleClass = MetaschemaTestSuiteModule.class)
public class GenerationCase implements IBoundObject {
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
   * "A URI reference to the expected schema file location."
   */
  @BoundFlag(
      formalName = "Location",
      description = "A URI reference to the expected schema file location.",
      name = "location",
      required = true,
      typeAdapter = UriReferenceAdapter.class)
  private URI _location;

  /**
   * "The expected result of content comparison."
   */
  @BoundFlag(
      formalName = "Match Result",
      description = "The expected result of content comparison.",
      name = "match-result",
      defaultValue = "MATCH",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
          @AllowedValue(value = "MATCH", description = "The actual content matched the expected content."),
          @AllowedValue(value = "MISMATCH", description = "The actual content did not match the expected content.") })))
  private String _matchResult;

  public GenerationCase() {
    this(null);
  }

  public GenerationCase(IMetaschemaData data) {
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

  public String getMatchResult() {
    return _matchResult;
  }

  public void setMatchResult(String value) {
    _matchResult = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
