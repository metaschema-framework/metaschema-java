/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelElement;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.ModelType;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

final class AssemblyBuilder
    extends AbstractModelBuilder<IAssemblyBuilder>
    implements IAssemblyBuilder {

  private String rootNamespace = "";
  private String rootName;

  private List<? extends IModelBuilder<?>> modelInstances;

  AssemblyBuilder() {
    // prevent direct instantiation
  }

  @Override
  public AssemblyBuilder reset() {
    super.reset();
    this.modelInstances = CollectionUtil.emptyList();
    return this;
  }

  @Override
  @NonNull
  public AssemblyBuilder rootNamespace(@NonNull String name) {
    this.rootNamespace = name;
    return this;
  }

  @Override
  @NonNull
  public AssemblyBuilder rootName(@NonNull String name) {
    this.rootName = name;
    return this;
  }

  @Override
  @NonNull
  public AssemblyBuilder rootQName(@NonNull IEnhancedQName qname) {
    this.rootName = qname.getLocalName();
    this.rootNamespace = qname.getNamespace();
    return this;
  }

  @Override
  public AssemblyBuilder modelInstances(@Nullable List<? extends IModelBuilder<?>> modelInstances) {
    this.modelInstances = modelInstances == null ? CollectionUtil.emptyList() : modelInstances;
    return this;
  }

  /**
   * Get the model instance builders configured for this assembly.
   *
   * @return the list of model instance builders
   */
  @NonNull
  List<? extends IModelBuilder<?>> getModelInstanceBuilders() {
    return this.modelInstances;
  }

  /**
   * Check if any model instances are references that need lazy resolution.
   *
   * @return {@code true} if any model instance is an {@link IModelReference}
   */
  boolean hasModelReferences() {
    return modelInstances.stream().anyMatch(IModelReference.class::isInstance);
  }

  @Override
  @NonNull
  public IAssemblyInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent) {
    IAssemblyDefinition def = toDefinition();
    return toInstance(parent, def);
  }

  /**
   * Build a mocked assembly instance, using the provided definition, as a child
   * of the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @param definition
   *          the definition to base the instance on
   * @return the new mocked instance
   */
  @Override
  @NonNull
  public IAssemblyInstanceAbsolute toInstance(
      @NonNull IAssemblyDefinition parent,
      @NonNull IAssemblyDefinition definition) {
    validate();

    IAssemblyInstanceAbsolute retval = mock(IAssemblyInstanceAbsolute.class);
    applyNamedInstance(retval, definition, parent);
    return retval;
  }

  /**
   * Build a mocked assembly definition.
   *
   * @return the new mocked definition
   */
  @Override
  @NonNull
  public IAssemblyDefinition toDefinition() {
    return toDefinition(null);
  }

  @Override
  @NonNull
  public IAssemblyDefinition toDefinition(@Nullable IModule module) {
    validate();

    // already validated as non-null
    ISource source = ObjectUtils.notNull(getSource());

    IAssemblyDefinition retval = mock(IAssemblyDefinition.class);
    applyDefinition(retval, module);

    Map<IEnhancedQName, IFlagInstance> flags = getFlags().stream()
        .map(builder -> builder.source(source).toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            IFlagInstance::getQName,
            Function.identity()));

    if (rootName != null) {
      doReturn(ModelType.ASSEMBLY).when(retval).getModelType();

      IEnhancedQName rootQName = IEnhancedQName.of(ObjectUtils.notNull(rootNamespace), ObjectUtils.notNull(rootName));
      doReturn(rootQName).when(retval).getRootQName();
    }

    doReturn(new AssemblyConstraintSet(source)).when(retval).getConstraintSupport();

    doReturn(flags.values()).when(retval).getFlagInstances();
    flags.entrySet().forEach(entry -> {
      assert entry != null;
      doReturn(entry.getValue()).when(retval).getFlagInstanceByName(eq(entry.getKey().getIndexPosition()));
    });

    Map<IEnhancedQName, ? extends INamedModelInstanceAbsolute> modelInstances = this.modelInstances.stream()
        .map(builder -> builder.source(source).toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            INamedModelInstanceAbsolute::getQName,
            Function.identity()));

    doReturn(modelInstances.values()).when(retval).getModelInstances();
    doReturn(CollectionUtil.emptyMap()).when(retval).getChoiceGroupInstances();
    doReturn(CollectionUtil.emptyList()).when(retval).getChoiceInstances();
    modelInstances.forEach((key, value) -> {
      doReturn(value).when(retval).getNamedModelInstanceByName(eq(key.getIndexPosition()));

      if (value instanceof IAssemblyInstance) {
        doReturn(value).when(retval).getAssemblyInstanceByName(eq(key.getIndexPosition()));
      } else if (value instanceof IFieldInstance) {
        doReturn(value).when(retval).getFieldInstanceByName(eq(key.getIndexPosition()));
      }
    });
    doReturn(
        modelInstances.values().stream()
            .filter(IAssemblyInstance.class::isInstance)
            .collect(Collectors.toList()))
                .when(retval).getAssemblyInstances();
    doReturn(
        modelInstances.values().stream()
            .filter(IFieldInstance.class::isInstance)
            .collect(Collectors.toList()))
                .when(retval).getFieldInstances();
    return retval;
  }

  /**
   * Build a mocked assembly definition without model instances. This is used for
   * two-phase construction when references need to be resolved later.
   *
   * @param module
   *          the containing module
   * @return the new mocked definition with empty model instances
   */
  @NonNull
  IAssemblyDefinition toDefinitionShell(@Nullable IModule module) {
    validate();

    // already validated as non-null
    ISource source = ObjectUtils.notNull(getSource());

    IAssemblyDefinition retval = mock(IAssemblyDefinition.class);
    applyDefinition(retval, module);

    Map<IEnhancedQName, IFlagInstance> flags = getFlags().stream()
        .map(builder -> builder.source(source).toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            IFlagInstance::getQName,
            Function.identity()));

    if (rootName != null) {
      doReturn(ModelType.ASSEMBLY).when(retval).getModelType();

      IEnhancedQName rootQName = IEnhancedQName.of(ObjectUtils.notNull(rootNamespace), ObjectUtils.notNull(rootName));
      doReturn(rootQName).when(retval).getRootQName();
    }

    doReturn(new AssemblyConstraintSet(source)).when(retval).getConstraintSupport();

    doReturn(flags.values()).when(retval).getFlagInstances();
    flags.entrySet().forEach(entry -> {
      assert entry != null;
      doReturn(entry.getValue()).when(retval).getFlagInstanceByName(eq(entry.getKey().getIndexPosition()));
    });

    // Initialize with empty model instances - will be populated later
    doReturn(CollectionUtil.emptyList()).when(retval).getModelInstances();
    doReturn(CollectionUtil.emptyMap()).when(retval).getChoiceGroupInstances();
    doReturn(CollectionUtil.emptyList()).when(retval).getChoiceInstances();
    doReturn(CollectionUtil.emptyList()).when(retval).getAssemblyInstances();
    doReturn(CollectionUtil.emptyList()).when(retval).getFieldInstances();

    return retval;
  }

  /**
   * Resolve model instances for an already-built definition, using the provided
   * definition maps to resolve references.
   *
   * @param definition
   *          the definition to add model instances to
   * @param assemblyDefinitions
   *          map of assembly name to definition for resolving assembly references
   * @param fieldDefinitions
   *          map of field name to definition for resolving field references
   */
  void resolveModelInstances(
      @NonNull IAssemblyDefinition definition,
      @NonNull Map<String, IAssemblyDefinition> assemblyDefinitions,
      @NonNull Map<String, IFieldDefinition> fieldDefinitions) {

    ISource source = ObjectUtils.notNull(getSource());
    List<INamedModelInstanceAbsolute> instances = new ArrayList<>();

    for (IModelBuilder<?> builder : modelInstances) {
      INamedModelInstanceAbsolute instance;
      if (builder instanceof IModelReference) {
        IModelReference ref = (IModelReference) builder;
        String refName = ref.getReferencedName();

        if (builder instanceof AssemblyReference) {
          IAssemblyDefinition refDef = assemblyDefinitions.get(refName);
          if (refDef == null) {
            throw new IllegalStateException("Assembly reference '" + refName + "' not found in module");
          }
          // Create an instance that references the existing definition
          IAssemblyBuilder instanceBuilder = IAssemblyBuilder.builder()
              .name(refName)
              .namespace(ObjectUtils.notNull(getNamespace()))
              .source(source);
          instance = instanceBuilder.toInstance(definition, refDef);
        } else if (builder instanceof FieldReference) {
          IFieldDefinition refDef = fieldDefinitions.get(refName);
          if (refDef == null) {
            throw new IllegalStateException("Field reference '" + refName + "' not found in module");
          }
          // Create an instance that references the existing definition
          IFieldBuilder instanceBuilder = IFieldBuilder.builder()
              .name(refName)
              .namespace(ObjectUtils.notNull(getNamespace()))
              .source(source);
          instance = instanceBuilder.toInstance(definition, refDef);
        } else {
          throw new IllegalStateException("Unknown reference type: " + builder.getClass().getName());
        }
      } else {
        // Regular builder - create instance normally
        instance = builder.source(source).toInstance(definition);
      }
      instances.add(instance);
    }

    // Wire up the instances
    Map<IEnhancedQName, INamedModelInstanceAbsolute> instanceMap = instances.stream()
        .collect(Collectors.toUnmodifiableMap(
            INamedModelInstanceAbsolute::getQName,
            Function.identity()));

    doReturn(new ArrayList<>(instanceMap.values())).when(definition).getModelInstances();
    instanceMap.forEach((key, value) -> {
      doReturn(value).when(definition).getNamedModelInstanceByName(eq(key.getIndexPosition()));

      if (value instanceof IAssemblyInstance) {
        doReturn(value).when(definition).getAssemblyInstanceByName(eq(key.getIndexPosition()));
      } else if (value instanceof IFieldInstance) {
        doReturn(value).when(definition).getFieldInstanceByName(eq(key.getIndexPosition()));
      }
    });

    List<IAssemblyInstance> assemblyInstances = instanceMap.values().stream()
        .filter(IAssemblyInstance.class::isInstance)
        .map(IAssemblyInstance.class::cast)
        .collect(Collectors.toList());
    doReturn(assemblyInstances).when(definition).getAssemblyInstances();

    List<IFieldInstance> fieldInstances = instanceMap.values().stream()
        .filter(IFieldInstance.class::isInstance)
        .map(IFieldInstance.class::cast)
        .collect(Collectors.toList());
    doReturn(fieldInstances).when(definition).getFieldInstances();
  }

  @Override
  protected void applyNamed(INamedModelElement element) {
    super.applyNamed(element);
    doReturn(ModelType.ASSEMBLY).when(element).getModelType();
  }
}
