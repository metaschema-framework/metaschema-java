/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.config.IBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.IChoiceGroupBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.IDefinitionBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChoiceGroupTypeInfoImpl
    extends AbstractModelInstanceTypeInfo<IChoiceGroupInstance>
    implements IChoiceGroupTypeInfo {

  /**
   * Create a type information object describing a choice group instance.
   *
   * @param instance
   *          the choice group instance to generate type information for
   * @param parent
   *          the type information for the parent assembly definition containing
   *          the choice group
   */
  public ChoiceGroupTypeInfoImpl(
      @NonNull IChoiceGroupInstance instance,
      @NonNull IAssemblyDefinitionTypeInfo parent) {
    super(instance, parent);
  }

  @Override
  public TypeName getJavaItemType() {
    return getParentTypeInfo().getTypeResolver().getClassName(getInstance());
  }

  /**
   * Get the Java field type for this choice group instance.
   *
   * <p>
   * Returns a collection type ({@link List} or {@link Map}) when maxOccurs allows
   * multiple items, or the item type directly for single-valued instances. When
   * binding configuration specifies a custom item type with wildcard usage
   * enabled, generates bounded wildcard types (e.g.,
   * {@code List<? extends Type>}).
   *
   * @return the Java field type for code generation
   */
  @NonNull
  @Override
  public TypeName getJavaFieldType() {
    TypeName item = getJavaItemType();

    @NonNull
    TypeName retval;
    IChoiceGroupInstance instance = getInstance();
    int maxOccurrence = instance.getMaxOccurs();
    if (maxOccurrence == -1 || maxOccurrence > 1) {
      // Check if we should use wildcard types
      TypeName collectionItemType = item;
      IAssemblyDefinition parent = instance.getContainingDefinition();
      ITypeResolver resolver = getParentTypeInfo().getTypeResolver();
      IBindingConfiguration bindingConfig = resolver.getBindingConfiguration();
      IDefinitionBindingConfiguration defConfig = bindingConfig.getBindingConfigurationForDefinition(parent);
      if (defConfig != null) {
        IChoiceGroupBindingConfiguration choiceConfig = defConfig.getChoiceGroupBindings()
            .get(instance.getGroupAsName());
        if (choiceConfig != null && choiceConfig.getItemTypeName() != null && choiceConfig.isUseWildcard()) {
          // Use wildcard type for flexibility
          collectionItemType = WildcardTypeName.subtypeOf(item);
        }
      }

      if (JsonGroupAsBehavior.KEYED.equals(instance.getJsonGroupAsBehavior())) {
        retval = ObjectUtils.notNull(
            ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), collectionItemType));
      } else {
        retval = ObjectUtils.notNull(ParameterizedTypeName.get(ClassName.get(List.class), collectionItemType));
      }
    } else {
      retval = item;
    }
    return retval;
  }

  @Override
  protected AnnotationSpec.Builder newBindingAnnotation() {
    return ObjectUtils.notNull(AnnotationSpec.builder(BoundChoiceGroup.class));
  }

  @SuppressWarnings({ "PMD.UseConcurrentHashMap", "PMD.NPathComplexity", "PMD.CyclomaticComplexity" })
  @Override
  public Set<IModelDefinition> buildBindingAnnotation(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder,
      AnnotationSpec.Builder annotation) {
    IChoiceGroupInstance choiceGroup = getInstance();

    String discriminator = choiceGroup.getJsonDiscriminatorProperty();
    if (!IChoiceGroupInstance.DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME.equals(discriminator)) {
      annotation.addMember("discriminator", "$S", discriminator);
    }

    int minOccurs = choiceGroup.getMinOccurs();
    if (minOccurs != IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS) {
      annotation.addMember("minOccurs", "$L", minOccurs);
    }

    int maxOccurs = choiceGroup.getMaxOccurs();
    if (maxOccurs != IGroupable.DEFAULT_GROUP_AS_MAX_OCCURS) {
      annotation.addMember("maxOccurs", "$L", maxOccurs);
    }

    if (maxOccurs == -1 || maxOccurs > 1) {
      // requires a group-as
      annotation.addMember("groupAs", "$L", generateGroupAsAnnotation().build());
    }

    String jsonKeyName = choiceGroup.getJsonKeyFlagInstanceName();
    if (jsonKeyName != null) {
      annotation.addMember("jsonKey", "$S", jsonKeyName);
    }

    Set<IModelDefinition> retval = new LinkedHashSet<>();

    IAssemblyDefinitionTypeInfo parentTypeInfo = getParentTypeInfo();
    ITypeResolver typeResolver = parentTypeInfo.getTypeResolver();

    Map<ClassName, List<INamedModelInstanceGrouped>> referencedDefinitions = new LinkedHashMap<>();
    Collection<? extends INamedModelInstanceGrouped> modelInstances = getInstance().getNamedModelInstances();
    for (INamedModelInstanceGrouped modelInstance : modelInstances) {
      ClassName className = typeResolver.getClassName(modelInstance.getDefinition());
      List<INamedModelInstanceGrouped> instances = referencedDefinitions.get(className);
      if (instances == null) {
        instances = new LinkedList<>(); // NOPMD needed
        referencedDefinitions.put(className, instances);
      }
      instances.add(modelInstance);
    }

    for (INamedModelInstanceGrouped modelInstance : modelInstances) {
      assert modelInstance != null;
      IGroupedNamedModelInstanceTypeInfo instanceTypeInfo = typeResolver.getTypeInfo(modelInstance, this);

      ClassName className = typeResolver.getClassName(modelInstance.getDefinition());
      retval.addAll(instanceTypeInfo.generateMemberAnnotation(
          annotation,
          typeBuilder,
          referencedDefinitions.get(className).size() > 1));
    }
    return retval;
  }

  /**
   * Get the binding configuration for this choice group, if one exists.
   *
   * @return the choice group binding configuration, or {@code null} if not
   *         configured
   */
  @Nullable
  private IChoiceGroupBindingConfiguration getChoiceGroupBindingConfiguration() {
    IChoiceGroupInstance instance = getInstance();
    IAssemblyDefinition parent = instance.getContainingDefinition();
    ITypeResolver resolver = getParentTypeInfo().getTypeResolver();
    IBindingConfiguration bindingConfig = resolver.getBindingConfiguration();
    IDefinitionBindingConfiguration defConfig = bindingConfig.getBindingConfigurationForDefinition(parent);
    if (defConfig != null) {
      return defConfig.getChoiceGroupBindings().get(instance.getGroupAsName());
    }
    return null;
  }

  @Override
  public void buildGetterJavadoc(@NonNull MethodSpec.Builder builder) {
    IChoiceGroupInstance instance = getInstance();
    String groupAsName = instance.getGroupAsName();

    builder.addJavadoc("Get the {@code $L} choice group items.\n", groupAsName);

    // Add item type information if configured
    IChoiceGroupBindingConfiguration choiceConfig = getChoiceGroupBindingConfiguration();
    if (choiceConfig != null) {
      String itemTypeName = choiceConfig.getItemTypeName();
      if (itemTypeName != null) {
        builder.addJavadoc("\n");
        builder.addJavadoc("<p>\n");
        String simpleTypeName = itemTypeName.substring(itemTypeName.lastIndexOf('.') + 1);
        if (choiceConfig.isUseWildcard()) {
          builder.addJavadoc("Items in this collection implement {@link $L}.\n", simpleTypeName);
        } else {
          builder.addJavadoc("Items in this collection are of type {@link $L}.\n", simpleTypeName);
        }
      }
    }

    builder.addJavadoc("\n");
    builder.addJavadoc("@return the $L items\n", groupAsName);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Generates Javadoc for choice group setter methods, including documentation of
   * the configured item type (if binding configuration specifies one) and whether
   * wildcard types are required.
   */
  @Override
  public void buildSetterJavadoc(@NonNull MethodSpec.Builder builder, @NonNull String paramName) {
    IChoiceGroupInstance instance = getInstance();
    String groupAsName = instance.getGroupAsName();

    builder.addJavadoc("Set the {@code $L} choice group items.\n", groupAsName);

    // Add item type information if configured
    IChoiceGroupBindingConfiguration choiceConfig = getChoiceGroupBindingConfiguration();
    if (choiceConfig != null) {
      String itemTypeName = choiceConfig.getItemTypeName();
      if (itemTypeName != null) {
        builder.addJavadoc("\n");
        builder.addJavadoc("<p>\n");
        String simpleTypeName = itemTypeName.substring(itemTypeName.lastIndexOf('.') + 1);
        if (choiceConfig.isUseWildcard()) {
          builder.addJavadoc("Items in this collection must implement {@link $L}.\n", simpleTypeName);
        } else {
          builder.addJavadoc("Items in this collection must be of type {@link $L}.\n", simpleTypeName);
        }
      }
    }

    builder.addJavadoc("\n");
    builder.addJavadoc("@param $L\n", paramName);
    builder.addJavadoc("          the $L items to set\n", groupAsName);
  }

}
