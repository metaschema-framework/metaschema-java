/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.io.BindingException;
import dev.metaschema.databind.model.annotations.BoundAny;

/**
 * Tests that the code generator properly emits {@link BoundAny}-annotated
 * fields for assemblies containing {@code <any/>} in their model.
 */
class AnyCodegenTest
    extends AbstractMetaschemaTest {

  @Test
  void testAnyFieldGenerated()
      throws MetaschemaException, IOException, ClassNotFoundException, BindingException,
      NoSuchMethodException {
    Class<? extends IBoundObject> rootClass = compileModule(
        ObjectUtils.notNull(Paths.get("src/test/resources/metaschema/any/metaschema.xml")),
        null,
        "com.example.ns.any_test.Root",
        ObjectUtils.notNull(generationDir));

    // Verify a field annotated with @BoundAny exists
    Field anyField = findBoundAnyField(rootClass);
    assertNotNull(anyField, "Generated class should have a field annotated with @BoundAny");

    // Verify the field type is IAnyContent
    assertEquals(IAnyContent.class, anyField.getType(),
        "@BoundAny field should be of type IAnyContent");

    // Verify getter exists and returns IAnyContent
    Method getter = rootClass.getMethod("getAny");
    assertNotNull(getter, "Generated class should have getAny() method");
    assertTrue(IAnyContent.class.isAssignableFrom(getter.getReturnType()),
        "getAny() should return IAnyContent");

    // Verify setter exists and accepts IAnyContent
    Method setter = rootClass.getMethod("setAny", IAnyContent.class);
    assertNotNull(setter, "Generated class should have setAny(IAnyContent) method");
  }

  /**
   * Find the field annotated with {@link BoundAny} in the given class.
   *
   * @param clazz
   *          the class to search
   * @return the field, or {@code null} if not found
   */
  private static Field findBoundAnyField(Class<?> clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(BoundAny.class)) {
        return field;
      }
    }
    return null;
  }
}
