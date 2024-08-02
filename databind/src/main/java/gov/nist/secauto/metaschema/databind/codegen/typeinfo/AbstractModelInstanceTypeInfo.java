/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractModelInstanceTypeInfo<INSTANCE extends IModelInstanceAbsolute>
    extends AbstractInstanceTypeInfo<INSTANCE, IAssemblyDefinitionTypeInfo>
    implements IModelInstanceTypeInfo {

  protected AbstractModelInstanceTypeInfo(
      @NonNull INSTANCE instance,
      @NonNull IAssemblyDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  public String getBaseName() {
    INSTANCE instance = getInstance();
    String baseName = getInstance().getGroupAsName();
    if (baseName == null) {
      throw new IllegalStateException(String.format(
          "Unable to derive the property name, due to missing group as name, for '%s' in the module '%s'.",
          instance.toCoordinates(),
          instance.getContainingModule().getLocation()));
    }
    return baseName;
  }

  @Override
  public @NonNull TypeName getJavaFieldType() {
    TypeName item = getJavaItemType();

    @NonNull TypeName retval;
    IModelInstanceAbsolute instance = getInstance();
    int maxOccurance = instance.getMaxOccurs();
    if (maxOccurance == -1 || maxOccurance > 1) {
      if (JsonGroupAsBehavior.KEYED.equals(instance.getJsonGroupAsBehavior())) {
        retval = ObjectUtils.notNull(
            ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), item));
      } else {
        retval = ObjectUtils.notNull(ParameterizedTypeName.get(ClassName.get(List.class), item));
      }
    } else {
      retval = item;
    }

    return retval;
  }

  @NonNull
  protected abstract AnnotationSpec.Builder newBindingAnnotation();

  @Override
  public Set<IModelDefinition> buildField(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder) {
    Set<IModelDefinition> retval = new LinkedHashSet<>(super.buildField(typeBuilder, fieldBuilder));

    AnnotationSpec.Builder annotation = newBindingAnnotation();

    retval.addAll(buildBindingAnnotation(typeBuilder, fieldBuilder, annotation));

    fieldBuilder.addAnnotation(annotation.build());

    return retval;
  }

  @NonNull
  protected AnnotationSpec.Builder generateGroupAsAnnotation() {
    AnnotationSpec.Builder groupAsAnnoation = AnnotationSpec.builder(GroupAs.class);

    IModelInstanceAbsolute modelInstance = getInstance();

    groupAsAnnoation.addMember("name", "$S",
        ObjectUtils.requireNonNull(modelInstance.getGroupAsName(), "The grouping name must be non-null"));

    // TODO: handle group-as namespace as a prefix

    JsonGroupAsBehavior jsonGroupAsBehavior = modelInstance.getJsonGroupAsBehavior();
    assert jsonGroupAsBehavior != null;
    if (!IGroupable.DEFAULT_JSON_GROUP_AS_BEHAVIOR.equals(jsonGroupAsBehavior)) {
      groupAsAnnoation.addMember("inJson", "$T.$L",
          JsonGroupAsBehavior.class, jsonGroupAsBehavior.toString());
    }

    XmlGroupAsBehavior xmlGroupAsBehavior = modelInstance.getXmlGroupAsBehavior();
    assert xmlGroupAsBehavior != null;
    if (!IGroupable.DEFAULT_XML_GROUP_AS_BEHAVIOR.equals(xmlGroupAsBehavior)) {
      groupAsAnnoation.addMember("inXml", "$T.$L",
          XmlGroupAsBehavior.class, xmlGroupAsBehavior.toString());
    }
    return groupAsAnnoation;
  }
}
