/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.ClassUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IModelDefinitionTypeInfo;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.lang.model.element.Modifier;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractNamedModelInstanceTypeInfo<INSTANCE extends INamedModelInstanceAbsolute>
    extends AbstractModelInstanceTypeInfo<INSTANCE>
    implements INamedModelInstanceTypeInfo {
  public AbstractNamedModelInstanceTypeInfo(
      @NonNull INSTANCE instance,
      @NonNull IAssemblyDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  public @NonNull String getBaseName() {
    INSTANCE modelInstance = getInstance();
    String retval;
    if (modelInstance.getMaxOccurs() == -1 || modelInstance.getMaxOccurs() > 1) {
      retval = super.getBaseName();
    } else {
      retval = modelInstance.getEffectiveName();
    }
    return retval;
  }

  @Override
  public String getItemBaseName() {
    return getInstance().getEffectiveName();
  }

  @Override
  public TypeName getJavaItemType() {
    return getParentTypeInfo().getTypeResolver().getClassName(this);
  }

  @Override
  public Set<IModelDefinition> buildField(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder) {
    Set<IModelDefinition> retval = super.buildField(typeBuilder, fieldBuilder);

    IModelDefinition definition = getInstance().getDefinition();
    if (definition.isInline() && (definition.hasChildren() || definition instanceof IAssemblyDefinition)) {
      retval = new LinkedHashSet<>(retval);

      // this is an inline definition that must be built as a child class
      retval.add(definition);
    }
    return retval.isEmpty() ? CollectionUtil.emptySet() : CollectionUtil.unmodifiableSet(retval);
  }

  @Override
  public Set<IModelDefinition> buildBindingAnnotation(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder,
      AnnotationSpec.Builder annotation) {

    buildBindingAnnotationCommon(annotation);

    INamedModelInstanceAbsolute instance = getInstance();

    int minOccurs = instance.getMinOccurs();
    if (minOccurs != IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS) {
      annotation.addMember("minOccurs", "$L", minOccurs);
    }

    int maxOccurs = instance.getMaxOccurs();
    if (maxOccurs != IGroupable.DEFAULT_GROUP_AS_MAX_OCCURS) {
      annotation.addMember("maxOccurs", "$L", maxOccurs);
    }
    if (maxOccurs == -1 || maxOccurs > 1) {
      // requires a group-as
      annotation.addMember("groupAs", "$L", generateGroupAsAnnotation().build());
    }

    return CollectionUtil.emptySet();
  }

  @Override
  protected void buildExtraMethods(TypeSpec.Builder builder, FieldSpec valueField) {
    super.buildExtraMethods(builder, valueField);

    INamedModelInstanceAbsolute instance = getInstance();
    int maxOccurance = instance.getMaxOccurs();
    if (maxOccurance == -1 || maxOccurance > 1) {
      TypeName itemType = getJavaItemType();
      ParameterSpec valueParam = ParameterSpec.builder(itemType, "item").build();

      String itemPropertyName = ClassUtils.toPropertyName(getItemBaseName());

      if (JsonGroupAsBehavior.KEYED.equals(instance.getJsonGroupAsBehavior())) {
        IFlagInstance jsonKey = instance.getDefinition().getJsonKey();
        if (jsonKey == null) {
          throw new IllegalStateException(
              String.format("JSON key not defined for property: %s", instance.toCoordinates()));
        }

        // get the json key property on the instance's definition
        ITypeResolver typeResolver = getParentTypeInfo().getTypeResolver();
        IModelDefinitionTypeInfo instanceTypeInfo = typeResolver.getTypeInfo(instance.getDefinition());
        IFlagInstanceTypeInfo jsonKeyTypeInfo = instanceTypeInfo.getFlagInstanceTypeInfo(jsonKey);

        if (jsonKeyTypeInfo == null) {
          throw new IllegalStateException(
              String.format("Unable to identify JSON key for property: %s", instance.toCoordinates()));
        }

        {
          // create add method
          MethodSpec.Builder method = MethodSpec.methodBuilder("add" + itemPropertyName)
              .addParameter(valueParam)
              .returns(itemType)
              .addModifiers(Modifier.PUBLIC)
              .addJavadoc("Add a new {@link $T} item to the underlying collection.\n", itemType)
              .addJavadoc("@param item the item to add\n")
              .addJavadoc("@return the existing {@link $T} item in the collection or {@code null} if not item exists\n",
                  itemType)
              .addStatement("$1T value = $2T.requireNonNull($3N,\"$3N value cannot be null\")",
                  itemType, ObjectUtils.class, valueParam)
              .addStatement("$1T key = $2T.requireNonNull($3N.$4N(),\"$3N key cannot be null\")",
                  String.class, ObjectUtils.class, valueParam, "get" + jsonKeyTypeInfo.getPropertyName())
              .beginControlFlow("if ($N == null)", valueField)
              .addStatement("$N = new $T<>()", valueField, LinkedHashMap.class)
              .endControlFlow()
              .addStatement("return $N.put(key, value)", valueField);

          builder.addMethod(method.build());
        }
        {
          // create remove method
          MethodSpec.Builder method = MethodSpec.methodBuilder("remove" + itemPropertyName)
              .addParameter(valueParam)
              .returns(TypeName.BOOLEAN)
              .addModifiers(Modifier.PUBLIC)
              .addJavadoc("Remove the {@link $T} item from the underlying collection.\n", itemType)
              .addJavadoc("@param item the item to remove\n")
              .addJavadoc("@return {@code true} if the item was removed or {@code false} otherwise\n")
              .addStatement("$1T value = $2T.requireNonNull($3N,\"$3N value cannot be null\")",
                  itemType, ObjectUtils.class, valueParam)
              .addStatement("$1T key = $2T.requireNonNull($3N.$4N(),\"$3N key cannot be null\")",
                  String.class, ObjectUtils.class, valueParam, "get" + jsonKeyTypeInfo.getPropertyName())
              .addStatement("return $1N != null && $1N.remove(key, value)", valueField);
          builder.addMethod(method.build());
        }
      } else {
        {
          // create add method
          MethodSpec.Builder method = MethodSpec.methodBuilder("add" + itemPropertyName)
              .addParameter(valueParam)
              .returns(TypeName.BOOLEAN)
              .addModifiers(Modifier.PUBLIC)
              .addJavadoc("Add a new {@link $T} item to the underlying collection.\n", itemType)
              .addJavadoc("@param item the item to add\n")
              .addJavadoc("@return {@code true}\n")
              .addStatement("$T value = $T.requireNonNull($N,\"$N cannot be null\")",
                  itemType, ObjectUtils.class, valueParam, valueParam)
              .beginControlFlow("if ($N == null)", valueField)
              .addStatement("$N = new $T<>()", valueField, LinkedList.class)
              .endControlFlow()
              .addStatement("return $N.add(value)", valueField);

          builder.addMethod(method.build());
        }

        {
          // create remove method
          MethodSpec.Builder method = MethodSpec.methodBuilder("remove" + itemPropertyName)
              .addParameter(valueParam)
              .returns(TypeName.BOOLEAN)
              .addModifiers(Modifier.PUBLIC)
              .addJavadoc("Remove the first matching {@link $T} item from the underlying collection.\n", itemType)
              .addJavadoc("@param item the item to remove\n")
              .addJavadoc("@return {@code true} if the item was removed or {@code false} otherwise\n")
              .addStatement("$T value = $T.requireNonNull($N,\"$N cannot be null\")",
                  itemType, ObjectUtils.class, valueParam, valueParam)
              .addStatement("return $1N != null && $1N.remove(value)", valueField);
          builder.addMethod(method.build());
        }
      }
    }
  }

}
