/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.testsuite;

import edu.umd.cs.findbugs.annotations.NonNull;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.lang.Override;
import java.lang.String;
import java.net.URI;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Reference to a metaschema module to load.
 */
@MetaschemaAssembly(
    formalName = "Metaschema",
    description = "Reference to a metaschema module to load.",
    name = "metaschema",
    moduleClass = MetaschemaTestSuiteModule.class)
public class Metaschema implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * A URI reference to the metaschema module location.
   */
  @BoundFlag(
      formalName = "Location",
      description = "A URI reference to the metaschema module location.",
      name = "location",
      required = true,
      typeAdapter = UriReferenceAdapter.class)
  private URI _location;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.Metaschema}
   * instance with no metadata.
   */
  public Metaschema() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.model.testing.testsuite.Metaschema}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public Metaschema(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the location.
   *
   * <p>
   * A URI reference to the metaschema module location.
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
   * A URI reference to the metaschema module location.
   *
   * @param value
   *          the location value to set
   */
  public void setLocation(@NonNull URI value) {
    _location = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
