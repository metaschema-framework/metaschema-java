/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.config.IBindingConfiguration;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Unit tests for {@link AbstractNamedModelInstanceTypeInfo#isRequired()}.
 *
 * <p>
 * Tests verify that the isRequired() method correctly accounts for:
 * <ul>
 * <li>minOccurs and maxOccurs values</li>
 * <li>Choice block membership (choiceId)</li>
 * </ul>
 */
class AbstractNamedModelInstanceTypeInfoTest {

  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  @NonNull
  private final IFieldInstanceAbsolute fieldInstance
      = ObjectUtils.notNull(context.mock(IFieldInstanceAbsolute.class));
  @NonNull
  private final IFieldDefinition fieldDefinition
      = ObjectUtils.notNull(context.mock(IFieldDefinition.class));
  @NonNull
  private final IAssemblyDefinitionTypeInfo parentTypeInfo
      = ObjectUtils.notNull(context.mock(IAssemblyDefinitionTypeInfo.class));

  /**
   * Creates a FieldInstanceTypeInfoImpl for testing with common mock setup.
   *
   * @param minOccurs
   *          the minimum occurrences
   * @param maxOccurs
   *          the maximum occurrences (-1 for unbounded)
   * @return the type info instance
   */
  @NonNull
  private FieldInstanceTypeInfoImpl createTypeInfo(int minOccurs, int maxOccurs) {
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(fieldInstance).getMinOccurs();
        will(returnValue(minOccurs));

        allowing(fieldInstance).getMaxOccurs();
        will(returnValue(maxOccurs));

        allowing(fieldInstance).getDefinition();
        will(returnValue(fieldDefinition));

        allowing(fieldInstance).getEffectiveName();
        will(returnValue("test-field"));
      }
    });

    return new FieldInstanceTypeInfoImpl(fieldInstance, parentTypeInfo);
  }

  /**
   * Test that a required single-valued property (minOccurs=1, maxOccurs=1)
   * outside a choice block returns true for isRequired().
   */
  @Test
  void testIsRequiredReturnsTrueForRequiredSingleProperty() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(1, 1);

    assertTrue(typeInfo.isRequired(),
        "Property with minOccurs=1 and maxOccurs=1 outside choice should be required");
  }

  /**
   * Test that an optional property (minOccurs=0) returns false for isRequired().
   */
  @Test
  void testIsRequiredReturnsFalseForOptionalProperty() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(0, 1);

    assertFalse(typeInfo.isRequired(),
        "Property with minOccurs=0 should not be required");
  }

  /**
   * Test that a collection property (maxOccurs > 1) returns false for
   * isRequired() even with minOccurs=1.
   */
  @Test
  void testIsRequiredReturnsFalseForCollectionProperty() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(1, -1);

    assertFalse(typeInfo.isRequired(),
        "Collection property (maxOccurs=-1) should not be required");
  }

  /**
   * Test that a collection property with fixed upper bound returns false for
   * isRequired().
   */
  @Test
  void testIsRequiredReturnsFalseForBoundedCollectionProperty() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(1, 5);

    assertFalse(typeInfo.isRequired(),
        "Collection property (maxOccurs=5) should not be required");
  }

  /**
   * Test that a property inside a choice block returns false for isRequired()
   * even when minOccurs=1 and maxOccurs=1.
   *
   * <p>
   * This is the key fix for issue #604: properties inside choice blocks are only
   * conditionally required (when that choice branch is taken), so for null-safety
   * purposes they must be treated as optional.
   */
  @Test
  void testIsRequiredReturnsFalseForPropertyInsideChoiceBlock() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(1, 1);

    // Before setting choiceId, should be required
    assertTrue(typeInfo.isRequired(),
        "Property should be required before being placed in a choice");

    // Set choiceId to simulate being inside a choice block
    typeInfo.setChoiceId("choice-1");

    // After setting choiceId, should NOT be required
    assertFalse(typeInfo.isRequired(),
        "Property inside choice block should not be required even with minOccurs=1");
  }

  /**
   * Test that an optional property inside a choice block remains not required.
   */
  @Test
  void testIsRequiredReturnsFalseForOptionalPropertyInsideChoiceBlock() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(0, 1);

    // Set choiceId
    typeInfo.setChoiceId("choice-1");

    assertFalse(typeInfo.isRequired(),
        "Optional property inside choice block should not be required");
  }

  /**
   * Test that clearing the choiceId restores the original required behavior.
   */
  @Test
  void testIsRequiredRestoredWhenChoiceIdCleared() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo(1, 1);

    // Set and then clear choiceId
    typeInfo.setChoiceId("choice-1");
    typeInfo.setChoiceId(null);

    assertTrue(typeInfo.isRequired(),
        "Property should be required again after choiceId is cleared");
  }
}
