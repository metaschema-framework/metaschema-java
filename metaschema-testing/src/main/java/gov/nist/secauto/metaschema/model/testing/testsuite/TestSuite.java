/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.testsuite;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import java.lang.Override;
import java.lang.String;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The root element containing a collection of test collections.
 */
@MetaschemaAssembly(
    formalName = "Test Suite",
    description = "The root element containing a collection of test collections.",
    name = "test-suite",
    moduleClass = MetaschemaTestSuiteModule.class,
    rootName = "test-suite")
public class TestSuite implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundAssembly(
      formalName = "Test Collection",
      description = "A collection of test scenarios located at a specific path.",
      useName = "test-collection",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "test-collections", inJson = JsonGroupAsBehavior.LIST))
  private List<TestCollection> _testCollections;

  public TestSuite() {
    this(null);
  }

  public TestSuite(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public List<TestCollection> getTestCollections() {
    return _testCollections;
  }

  public void setTestCollections(List<TestCollection> value) {
    _testCollections = value;
  }

  /**
   * Add a new {@link TestCollection} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addTestCollection(TestCollection item) {
    TestCollection value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_testCollections == null) {
      _testCollections = new LinkedList<>();
    }
    return _testCollections.add(value);
  }

  /**
   * Remove the first matching {@link TestCollection} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeTestCollection(TestCollection item) {
    TestCollection value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _testCollections != null && _testCollections.remove(value);
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
