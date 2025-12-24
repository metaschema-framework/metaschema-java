/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.testsuite;

import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.lang.Override;
import java.lang.String;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collection of test scenarios located at a specific path.
 */
@MetaschemaAssembly(
    formalName = "Test Collection",
    description = "A collection of test scenarios located at a specific path.",
    name = "test-collection",
    moduleClass = MetaschemaTestSuiteModule.class)
public class TestCollection implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * "A URI reference to the location of this test collection."
   */
  @BoundFlag(
      formalName = "Location",
      description = "A URI reference to the location of this test collection.",
      name = "location",
      required = true,
      typeAdapter = UriReferenceAdapter.class)
  private URI _location;

  /**
   * "The name of this test collection."
   */
  @BoundFlag(
      formalName = "Name",
      description = "The name of this test collection.",
      name = "name",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _name;

  @BoundAssembly(
      formalName = "Test Scenario",
      description = "A test scenario that validates a metaschema and its content.",
      useName = "test-scenario",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "test-scenarios", inJson = JsonGroupAsBehavior.LIST))
  private List<TestScenario> _testScenarios;

  public TestCollection() {
    this(null);
  }

  public TestCollection(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public URI getLocation() {
    return _location;
  }

  public void setLocation(URI value) {
    _location = value;
  }

  public String getName() {
    return _name;
  }

  public void setName(String value) {
    _name = value;
  }

  public List<TestScenario> getTestScenarios() {
    return _testScenarios;
  }

  public void setTestScenarios(List<TestScenario> value) {
    _testScenarios = value;
  }

  /**
   * Add a new {@link TestScenario} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addTestScenario(TestScenario item) {
    TestScenario value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_testScenarios == null) {
      _testScenarios = new LinkedList<>();
    }
    return _testScenarios.add(value);
  }

  /**
   * Remove the first matching {@link TestScenario} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeTestScenario(TestScenario item) {
    TestScenario value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _testScenarios != null && _testScenarios.remove(value);
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
