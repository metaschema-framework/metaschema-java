/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IModelDefinitionTypeInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Abstract base class for generating Java property code including fields,
 * getters, and setters with appropriate null-safety annotations.
 *
 * <h2>Null-Safety Annotation Contract</h2>
 *
 * <p>
 * Generated getters and setters receive null-safety annotations based on two
 * factors: whether the property is required and whether it is a collection.
 *
 * <h3>Getter Annotations</h3>
 * <ul>
 * <li>{@code @NonNull} - Collection properties (lazy initialized) or required
 * properties</li>
 * <li>{@code @Nullable} - Optional non-collection properties</li>
 * </ul>
 *
 * <h3>Setter Annotations</h3>
 * <ul>
 * <li>{@code @NonNull} - Collection properties or required properties</li>
 * <li>{@code @Nullable} - Optional non-collection properties</li>
 * </ul>
 *
 * <h2>Collection Handling</h2>
 *
 * <p>
 * Collection properties (where {@link #isCollectionType()} returns
 * {@code true}) use lazy initialization in their getters. The getter checks if
 * the field is null and initializes it with a new collection instance from
 * {@link #getCollectionImplementationClass()} before returning. This ensures
 * collection getters never return null, allowing them to be annotated
 * {@code @NonNull}.
 *
 * <p>
 * <strong>Contract:</strong> Subclasses must ensure that
 * {@link #isCollectionType()} and {@link #getCollectionImplementationClass()}
 * are consistent: {@code isCollectionType()} returns {@code true} if and only
 * if {@code getCollectionImplementationClass()} returns non-null.
 *
 * @param <PARENT>
 *          the type of the parent definition type info
 * @see IPropertyTypeInfo#isCollectionType()
 * @see IPropertyTypeInfo#getCollectionImplementationClass()
 */
public abstract class AbstractPropertyTypeInfo<PARENT extends IDefinitionTypeInfo>
    extends AbstractTypeInfo<PARENT>
    implements IPropertyTypeInfo {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Construct a new type information for a Java property.
   *
   * @param parentDefinition
   *          the definition containing the data this property is based on
   */
  protected AbstractPropertyTypeInfo(@NonNull PARENT parentDefinition) {
    super(parentDefinition);
  }

  @Override
  public Set<IModelDefinition> build(@NonNull TypeSpec.Builder builder) {

    TypeName javaFieldType = getJavaFieldType();
    FieldSpec.Builder field = FieldSpec.builder(javaFieldType, getJavaFieldName())
        .addModifiers(Modifier.PRIVATE);
    assert field != null;

    final Set<IModelDefinition> retval = buildField(builder, field);

    FieldSpec valueField = ObjectUtils.notNull(field.build());
    builder.addField(valueField);

    buildExtraMethods(builder, valueField);
    return retval;
  }

  /**
   * Build getter and setter methods for this property.
   *
   * <p>
   * This method generates accessor methods with appropriate null-safety
   * annotations and Javadoc based on the property's characteristics. Collection
   * getters use lazy initialization to ensure they never return null.
   *
   * @param typeBuilder
   *          the class builder to add methods to
   * @param fieldBuilder
   *          the field spec for the backing field
   */
  protected void buildExtraMethods(
      @NonNull TypeSpec.Builder typeBuilder,
      @NonNull FieldSpec fieldBuilder) {

    TypeName javaFieldType = getJavaFieldType();
    String propertyName = getPropertyName();

    // Get superinterfaces to check for method overrides
    List<ClassName> superinterfaces = getSuperinterfaces();

    {
      String getterName = "get" + propertyName;
      Class<?> collectionImplClass = getCollectionImplementationClass();
      // Collections are always @NonNull (lazy initialized), otherwise based on
      // isRequired()
      Class<?> nullAnnotation = collectionImplClass != null || isRequired() ? NonNull.class : Nullable.class;
      MethodSpec.Builder method = MethodSpec.methodBuilder(getterName)
          .returns(javaFieldType)
          .addAnnotation(AnnotationSpec.builder(nullAnnotation).build())
          .addModifiers(Modifier.PUBLIC);
      assert method != null;

      // Add @Override if the method is declared in a superinterface
      if (isMethodDeclaredInSuperinterfaces(getterName, superinterfaces)) {
        method.addAnnotation(Override.class);
      }

      buildGetterJavadoc(method);

      if (collectionImplClass != null) {
        // Use lazy initialization for collections
        method.beginControlFlow("if ($N == null)", fieldBuilder)
            .addStatement("$N = new $T<>()", fieldBuilder, collectionImplClass)
            .endControlFlow();
        // Collection is guaranteed non-null after lazy init
        method.addStatement("return $T.notNull($N)", ObjectUtils.class, fieldBuilder);
      } else {
        // Single-valued field - return directly (may be null for optional fields)
        method.addStatement("return $N", fieldBuilder);
      }
      typeBuilder.addMethod(method.build());
    }

    {
      String setterName = "set" + propertyName;
      // Add null-safety annotation to setter parameter
      // Collections get @NonNull (lazy initialized), required properties get @NonNull
      ParameterSpec.Builder paramBuilder = ParameterSpec.builder(javaFieldType, "value");
      Class<?> paramAnnotation = isCollectionType() || isRequired() ? NonNull.class : Nullable.class;
      paramBuilder.addAnnotation(AnnotationSpec.builder(paramAnnotation).build());
      ParameterSpec valueParam = paramBuilder.build();
      MethodSpec.Builder method = MethodSpec.methodBuilder(setterName)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(valueParam);
      assert method != null;

      // Add @Override if the method is declared in a superinterface
      if (isMethodDeclaredInSuperinterfaces(setterName, superinterfaces, javaFieldType)) {
        method.addAnnotation(Override.class);
      }

      buildSetterJavadoc(method, "value");
      method.addStatement("$N = $N", fieldBuilder, valueParam);
      typeBuilder.addMethod(method.build());
    }
  }

  /**
   * Get the superinterfaces configured for the parent definition.
   *
   * @return the list of superinterfaces, or an empty list if none
   */
  @NonNull
  private List<ClassName> getSuperinterfaces() {
    IDefinitionTypeInfo parent = getParentTypeInfo();
    if (parent instanceof IModelDefinitionTypeInfo) {
      return ((IModelDefinitionTypeInfo) parent).getSuperinterfaces();
    }
    return CollectionUtil.emptyList();
  }

  /**
   * Get the base class name configured for the parent definition.
   *
   * @return the base class name, or {@code null} if none
   */
  @Nullable
  private ClassName getBaseClassName() {
    IDefinitionTypeInfo parent = getParentTypeInfo();
    if (parent instanceof IModelDefinitionTypeInfo) {
      return ((IModelDefinitionTypeInfo) parent).getBaseClassName();
    }
    return null;
  }

  /**
   * Checks if a method with no parameters is declared in any of the
   * superinterfaces or the base class.
   *
   * @param methodName
   *          the method name to check
   * @param superinterfaces
   *          the list of superinterface class names
   * @return {@code true} if the method is declared in any supertype
   */
  private boolean isMethodDeclaredInSuperinterfaces(
      @NonNull String methodName,
      @NonNull List<ClassName> superinterfaces) {
    // Check base class first
    ClassName baseClassName = getBaseClassName();
    if (baseClassName != null && isMethodDeclaredInClass(methodName, baseClassName, 0)) {
      return true;
    }

    // Check superinterfaces
    for (ClassName superinterface : superinterfaces) {
      assert superinterface != null;
      if (isMethodDeclaredInClass(methodName, superinterface, 0)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a method with a single parameter of the specified type is declared
   * in any of the superinterfaces or the base class.
   *
   * @param methodName
   *          the method name to check
   * @param superinterfaces
   *          the list of superinterface class names
   * @param parameterType
   *          the expected parameter type
   * @return {@code true} if the method is declared in any supertype
   */
  private boolean isMethodDeclaredInSuperinterfaces(
      @NonNull String methodName,
      @NonNull List<ClassName> superinterfaces,
      @NonNull TypeName parameterType) {
    // Check base class first
    ClassName baseClassName = getBaseClassName();
    if (baseClassName != null && isMethodDeclaredInClass(methodName, baseClassName, parameterType)) {
      return true;
    }

    // Check superinterfaces
    for (ClassName superinterface : superinterfaces) {
      assert superinterface != null;
      if (isMethodDeclaredInClass(methodName, superinterface, parameterType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a method with no parameters is declared in the specified class or
   * any of its supertypes (superclasses and superinterfaces).
   * <p>
   * Uses {@link Class#getMethods()} which returns all public methods including
   * those inherited from superclasses and superinterfaces.
   *
   * @param methodName
   *          the method name to check
   * @param className
   *          the class name to search
   * @param parameterCount
   *          the expected number of parameters
   * @return {@code true} if the method is found
   */
  private static boolean isMethodDeclaredInClass(
      @NonNull String methodName,
      @NonNull ClassName className,
      int parameterCount) {
    try {
      Class<?> clazz = Class.forName(className.reflectionName());
      // getMethods() returns all public methods including inherited ones
      for (Method method : clazz.getMethods()) {
        if (method.getName().equals(methodName)
            && method.getParameterCount() == parameterCount) {
          return true;
        }
      }
    } catch (@SuppressWarnings("unused") ClassNotFoundException ex) {
      logClassNotFound(className.reflectionName(), methodName);
    }
    return false;
  }

  /**
   * Checks if a method with a single parameter of the specified type is declared
   * in the specified class or any of its supertypes.
   * <p>
   * Uses {@link Class#getMethods()} which returns all public methods including
   * those inherited from superclasses and superinterfaces.
   *
   * @param methodName
   *          the method name to check
   * @param className
   *          the class name to search
   * @param parameterType
   *          the expected parameter type
   * @return {@code true} if the method is found
   */
  private static boolean isMethodDeclaredInClass(
      @NonNull String methodName,
      @NonNull ClassName className,
      @NonNull TypeName parameterType) {
    try {
      Class<?> clazz = Class.forName(className.reflectionName());
      // getMethods() returns all public methods including inherited ones
      for (Method method : clazz.getMethods()) {
        if (method.getName().equals(methodName)
            && method.getParameterCount() == 1
            && isTypeMatch(ObjectUtils.notNull(method.getParameterTypes()[0]), parameterType)) {
          return true;
        }
      }
    } catch (@SuppressWarnings("unused") ClassNotFoundException ex) {
      logClassNotFound(className.reflectionName(), methodName);
    }
    return false;
  }

  /**
   * Logs a warning when a class cannot be found on the classpath during code
   * generation.
   *
   * @param className
   *          the fully qualified name of the class that was not found
   * @param methodName
   *          the method name being checked for override
   */
  private static void logClassNotFound(@NonNull String className, @NonNull String methodName) {
    if (LOGGER.isWarnEnabled()) {
      LOGGER.warn("Class '{}' not found on classpath during code generation. "
          + "The @Override annotation for method '{}' will be omitted. "
          + "Ensure the class is available as a dependency of the code generation plugin.",
          className, methodName);
    }
  }

  /**
   * Checks if a Class matches a JavaPoet TypeName.
   * <p>
   * Handles raw types, parameterized types (by comparing raw type), and
   * primitives.
   *
   * @param clazz
   *          the Class from reflection
   * @param typeName
   *          the TypeName from JavaPoet
   * @return {@code true} if they represent the same type
   */
  private static boolean isTypeMatch(@NonNull Class<?> clazz, @NonNull TypeName typeName) {
    // For parameterized types, extract the raw type name
    TypeName rawType = typeName;
    if (typeName instanceof com.squareup.javapoet.ParameterizedTypeName) {
      rawType = ((com.squareup.javapoet.ParameterizedTypeName) typeName).rawType;
    }

    // Compare canonical names
    String className = clazz.getCanonicalName();
    String typeNameString = rawType.toString();

    return className != null && className.equals(typeNameString);
  }

  /**
   * Generate the Java field associated with this property.
   *
   * @param typeBuilder
   *          the class builder the field is on
   * @param fieldBuilder
   *          the field builder
   * @return the set of definitions used by this field
   */
  protected Set<IModelDefinition> buildField(
      @NonNull TypeSpec.Builder typeBuilder,
      @NonNull FieldSpec.Builder fieldBuilder) {
    buildFieldJavadoc(fieldBuilder);
    return CollectionUtil.emptySet();
  }
}
