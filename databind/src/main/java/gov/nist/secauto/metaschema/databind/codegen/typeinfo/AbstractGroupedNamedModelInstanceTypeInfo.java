/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractGroupedNamedModelInstanceTypeInfo<I extends INamedModelInstanceGrouped>
    implements IGroupedNamedModelInstanceTypeInfo {
  @NonNull
  private final I instance;
  @NonNull
  private final IChoiceGroupTypeInfo parentTypeInfo;

  /**
   * Constructs a new type information object for a grouped named model instance.
   *
   * @param instance
   *          the grouped named model instance
   * @param parentTypeInfo
   *          the type information for the parent choice group containing this
   *          instance
   */
  protected AbstractGroupedNamedModelInstanceTypeInfo(
      @NonNull I instance,
      @NonNull IChoiceGroupTypeInfo parentTypeInfo) {
    this.instance = instance;
    this.parentTypeInfo = parentTypeInfo;
  }

  /**
   * Get the binding annotation class to use for this grouped instance.
   *
   * @return the annotation class
   */
  protected abstract Class<? extends Annotation> getBindingAnnotation();

  /**
   * Applies the instance annotation to the choice group annotation.
   *
   * @param instanceAnnotation
   *          the instance annotation builder containing the member details
   * @param choiceGroupAnnotation
   *          the choice group annotation builder to add the member to
   */
  protected abstract void applyInstanceAnnotation(
      @NonNull AnnotationSpec.Builder instanceAnnotation,
      @NonNull AnnotationSpec.Builder choiceGroupAnnotation);

  /**
   * Get the grouped named model instance.
   *
   * @return the instance
   */
  @NonNull
  protected I getInstance() {
    return instance;
  }

  /**
   * Get the type information for the parent choice group.
   *
   * @return the choice group type information
   */
  protected IChoiceGroupTypeInfo getChoiceGroupTypeInfo() {
    return parentTypeInfo;
  }

  @Override
  public Set<IModelDefinition> generateMemberAnnotation(
      @NonNull AnnotationSpec.Builder choiceGroupAnnotation,
      @NonNull TypeSpec.Builder typeBuilder,
      boolean requireExtension) {

    AnnotationSpec.Builder memberAnnotation = ObjectUtils.notNull(AnnotationSpec.builder(getBindingAnnotation()));

    I instance = getInstance();

    TypeInfoUtils.buildCommonBindingAnnotationValues(instance, memberAnnotation);

    // Add discriminatorValue if it differs from useName
    String discriminatorValue = instance.getEffectiveDisciminatorValue();
    String useName = instance.getEffectiveName();
    if (!discriminatorValue.equals(useName)) {
      memberAnnotation.addMember("discriminatorValue", "$S", discriminatorValue);
    }

    Set<IModelDefinition> retval = new LinkedHashSet<>();
    IModelDefinition definition = instance.getDefinition();

    IChoiceGroupTypeInfo choiceGroupTypeInfo = getChoiceGroupTypeInfo();
    ITypeResolver typeResolver = choiceGroupTypeInfo.getParentTypeInfo().getTypeResolver();

    ClassName itemTypeName;
    if (definition.isInline()) {
      // these definitions will be generated as standalone child classes
      itemTypeName = typeResolver.getClassName(definition);
      retval.add(definition);
    } else if (requireExtension) {
      // these definitions will be generated as an extension of a global class
      ClassName extendedClassName = typeResolver.getClassName(definition);
      itemTypeName = typeResolver.getSubclassName(
          choiceGroupTypeInfo.getParentTypeInfo().getClassName(),
          ObjectUtils.notNull(StringUtils.capitalize(instance.getEffectiveDisciminatorValue())),
          definition);

      TypeSpec.Builder subClass = TypeSpec.classBuilder(itemTypeName);
      subClass.superclass(extendedClassName);
      subClass.addModifiers(Modifier.PUBLIC, Modifier.STATIC); // , Modifier.FINAL);
      // subClass.addField(
      // FieldSpec.builder(String.class, "DISCRIMINATOR", Modifier.PUBLIC,
      // Modifier.STATIC, Modifier.FINAL)
      // .initializer("\"" + instance.getEffectiveDisciminatorValue() + "\"")
      // .build());
      typeBuilder.addType(subClass.build());
    } else {
      // reference the global class
      itemTypeName = typeResolver.getClassName(definition);
    }

    memberAnnotation.addMember("binding", "$T.class", itemTypeName);

    applyInstanceAnnotation(memberAnnotation, choiceGroupAnnotation);

    return retval;
  }

}
