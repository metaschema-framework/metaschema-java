/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.testing.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ModelUtil;

import java.lang.reflect.Field;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class ModelTestBase {
  public static void assertAssemblyDefinition(
      @NonNull Class<?> assemblyClass,
      @NonNull IBoundDefinitionModelAssembly assembly) {
    MetaschemaAssembly annotation = assemblyClass.getAnnotation(MetaschemaAssembly.class);

    assertAll(
        "assembly failed",
        () -> assertEquals(
            annotation.name(),
            assembly.getName(),
            "rootName"),
        () -> assertEquals(
            annotation.moduleClass(),
            assembly.getContainingModule().getClass(),
            "moduleClass"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.formalName()),
            assembly.getFormalName(),
            "formalName"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.description()),
            Optional.ofNullable(assembly.getDescription()).map(MarkupLine::toMarkdown).orElse(null),
            "description"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.remarks()),
            Optional.ofNullable(assembly.getRemarks()).map(MarkupMultiline::toMarkdown).orElse(null),
            "remarks"),
        () -> {
          String rootName = ModelUtil.resolveNoneOrValue(annotation.rootName());
          if (rootName != null) {
            assertAll(
                () -> assertEquals(
                    rootName,
                    assembly.getRootName(),
                    "rootName"),
                () -> assertEquals(
                    assembly.getContainingModule().getXmlNamespace().toASCIIString(),
                    assembly.getRootQName().getNamespace(),
                    "rootNamespace"),
                () -> assertTrue(true));
          } else {
            assertEquals(
                null,
                assembly.getRootQName(),
                "rootNamespace");
          }
        });
  }

  public static void assertFlagInstance(
      @NonNull Class<?> fieldOrAssemblyClass,
      @NonNull String flagJavaFieldName,
      @NonNull IBoundInstanceFlag flag,
      @NonNull IBindingContext context) throws NoSuchFieldException, SecurityException {
    Field field = fieldOrAssemblyClass.getDeclaredField(flagJavaFieldName);
    BoundFlag annotation = field.getAnnotation(BoundFlag.class);

    IDataTypeAdapter<?> adapter = ModelUtil.getDataTypeAdapter(annotation.typeAdapter(), context);

    String name = Optional.ofNullable(ModelUtil.resolveNoneOrValue(annotation.name())).orElse(field.getName());

    assertAll(
        flagJavaFieldName + " flag failed",
        () -> assertEquals(
            name,
            flag.getName(),
            "name"),
        () -> assertNull(
            flag.getUseName(),
            "useNname"),
        () -> assertEquals(
            adapter,
            flag.getDefinition().getJavaTypeAdapter(),
            "typeAdapter"),
        () -> assertEquals(
            annotation.required(),
            flag.isRequired(),
            "required"),
        () -> assertEquals(
            ModelUtil.resolveDefaultValue(annotation.defaultValue(), adapter),
            flag.getDefaultValue(),
            "defaultValue"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.formalName()),
            flag.getFormalName(),
            "formalName"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.description()),
            Optional.ofNullable(flag.getDescription()).map(MarkupLine::toMarkdown).orElse(null),
            "description"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.remarks()),
            Optional.ofNullable(flag.getRemarks()).map(MarkupMultiline::toMarkdown).orElse(null),
            "remarks"));
  }

  public static void assertFieldInstance(
      @NonNull Class<?> assemblyClass,
      @NonNull String fieldJavaFieldName,
      @NonNull IBoundInstanceModelField<?> field,
      @NonNull IBindingContext context) throws NoSuchFieldException, SecurityException {
    Field javaField = assemblyClass.getDeclaredField(fieldJavaFieldName);
    BoundField annotation = javaField.getAnnotation(BoundField.class);

    IDataTypeAdapter<?> adapter = ModelUtil.getDataTypeAdapter(annotation.typeAdapter(), context);

    String name;
    String useName;
    if (field.getDefinition().hasChildren()) {
      name = field.getDefinition().getName();
      useName = ModelUtil.resolveNoneOrValue(annotation.useName());
    } else {
      name = Optional.ofNullable(ModelUtil.resolveNoneOrValue(annotation.useName())).orElse(javaField.getName());
      useName = null;
    }

    assertAll(
        fieldJavaFieldName + " field failed",
        () -> assertEquals(
            name,
            field.getName(),
            "name"),
        () -> assertEquals(
            useName,
            field.getUseName(),
            "useName"),
        () -> assertEquals(
            adapter,
            field.getDefinition().getJavaTypeAdapter(),
            "typeAdapter"),
        () -> assertEquals(
            ModelUtil.resolveDefaultValue(annotation.defaultValue(), adapter),
            field.getDefaultValue(),
            "defaultValue"),
        () -> assertEquals(
            field.getContainingModule().getXmlNamespace().toASCIIString(),
            field.getQName().getNamespace(),
            "namespace"),
        () -> assertEquals(
            annotation.inXmlWrapped(),
            field.isInXmlWrapped(),
            "inXmlWrapped"),
        () -> assertEquals(
            annotation.minOccurs(),
            field.getMinOccurs(),
            "minOccurs"),
        () -> assertEquals(
            annotation.maxOccurs(),
            field.getMaxOccurs(),
            "maxOccurs"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.formalName()),
            field.getFormalName(),
            "formalName"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.description()),
            Optional.ofNullable(field.getDescription()).map(MarkupLine::toMarkdown).orElse(null),
            "description"),
        () -> assertEquals(
            ModelUtil.resolveNoneOrValue(annotation.remarks()),
            Optional.ofNullable(field.getRemarks()).map(MarkupMultiline::toMarkdown).orElse(null),
            "remarks"));
    // groupAs
  }
}
