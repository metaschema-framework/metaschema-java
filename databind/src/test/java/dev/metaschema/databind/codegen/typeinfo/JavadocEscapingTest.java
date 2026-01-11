/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.javapoet.MethodSpec;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.element.Modifier;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.config.IBindingConfiguration;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Unit tests for Javadoc generation with special characters.
 *
 * <p>
 * Tests verify that formal names containing special characters like ampersands
 * are properly escaped using {@code @literal} to prevent Javadoc errors.
 */
class JavadocEscapingTest {

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
   * Creates a FieldInstanceTypeInfoImpl for testing with the specified formal
   * name.
   *
   * @param formalName
   *          the formal name to use (may contain special characters)
   * @param propertyName
   *          the property name
   * @return the type info instance
   */
  @NonNull
  private FieldInstanceTypeInfoImpl createTypeInfo(String formalName, String propertyName) {
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(fieldInstance).getMinOccurs();
        will(returnValue(1));

        allowing(fieldInstance).getMaxOccurs();
        will(returnValue(1));

        allowing(fieldInstance).getDefinition();
        will(returnValue(fieldDefinition));

        allowing(fieldInstance).getEffectiveName();
        will(returnValue(propertyName));

        allowing(fieldInstance).getEffectiveFormalName();
        will(returnValue(formalName));

        allowing(fieldInstance).getEffectiveDescription();
        will(returnValue((MarkupLine) null));
      }
    });

    return new FieldInstanceTypeInfoImpl(fieldInstance, parentTypeInfo);
  }

  /**
   * Test that formal names containing ampersands are properly escaped using
   * {@literal} in getter Javadoc.
   *
   * <p>
   * This prevents Javadoc errors like "semicolon missing" when processing names
   * like "POA&M" (Plan of Action & Milestones).
   */
  @Test
  void testGetterJavadocEscapesAmpersand() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo("POA&M", "poam");

    MethodSpec.Builder builder = MethodSpec.methodBuilder("getPoam")
        .addModifiers(Modifier.PUBLIC);

    typeInfo.buildGetterJavadoc(builder);

    String javadoc = builder.build().javadoc.toString();

    assertTrue(javadoc.contains("{@literal POA&M}"),
        "Getter Javadoc should use {@literal} to escape ampersand. Got: " + javadoc);
    assertTrue(javadoc.contains("\""),
        "Getter Javadoc should wrap formal name in quotes. Got: " + javadoc);
  }

  /**
   * Test that formal names containing ampersands are properly escaped using
   * {@literal} in setter Javadoc.
   */
  @Test
  void testSetterJavadocEscapesAmpersand() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo("POA&M", "poam");

    MethodSpec.Builder builder = MethodSpec.methodBuilder("setPoam")
        .addModifiers(Modifier.PUBLIC);

    typeInfo.buildSetterJavadoc(builder, "value");

    String javadoc = builder.build().javadoc.toString();

    assertTrue(javadoc.contains("{@literal POA&M}"),
        "Setter Javadoc should use {@literal} to escape ampersand. Got: " + javadoc);
    assertTrue(javadoc.contains("\""),
        "Setter Javadoc should wrap formal name in quotes. Got: " + javadoc);
  }

  /**
   * Test that formal names without special characters still work correctly.
   */
  @Test
  void testJavadocWithNormalFormalName() {
    FieldInstanceTypeInfoImpl typeInfo = createTypeInfo("Control Identifier", "controlId");

    MethodSpec.Builder builder = MethodSpec.methodBuilder("getControlId")
        .addModifiers(Modifier.PUBLIC);

    typeInfo.buildGetterJavadoc(builder);

    String javadoc = builder.build().javadoc.toString();

    assertTrue(javadoc.contains("{@literal Control Identifier}"),
        "Getter Javadoc should use {@literal} for formal names. Got: " + javadoc);
  }
}
