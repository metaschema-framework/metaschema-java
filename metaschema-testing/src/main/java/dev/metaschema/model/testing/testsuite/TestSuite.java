/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../metaschema/unit-tests.yaml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.model.testing.testsuite;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundAssembly;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;

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

  /**
   * A collection of test scenarios located at a specific path.
   */
  @BoundAssembly(
      formalName = "Test Collection",
      description = "A collection of test scenarios located at a specific path.",
      useName = "test-collection",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "test-collections", inJson = JsonGroupAsBehavior.LIST))
  private List<TestCollection> _testCollections;

  /**
   * Constructs a new {@code dev.metaschema.model.testing.testsuite.TestSuite}
   * instance with no metadata.
   */
  public TestSuite() {
    this(null);
  }

  /**
   * Constructs a new {@code dev.metaschema.model.testing.testsuite.TestSuite}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public TestSuite(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the test Collection.
   *
   * <p>
   * A collection of test scenarios located at a specific path.
   *
   * @return the test-collection value
   */
  @NonNull
  public List<TestCollection> getTestCollections() {
    if (_testCollections == null) {
      _testCollections = new LinkedList<>();
    }
    return ObjectUtils.notNull(_testCollections);
  }

  /**
   * Set the test Collection.
   *
   * <p>
   * A collection of test scenarios located at a specific path.
   *
   * @param value
   *          the test-collection value to set
   */
  public void setTestCollections(@NonNull List<TestCollection> value) {
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
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
