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
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelElement;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.ModelType;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    validate();

    // already validated as non-null
    ISource source = ObjectUtils.notNull(getSource());

    IAssemblyDefinition retval = mock(IAssemblyDefinition.class);
    applyDefinition(retval);

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
            .flatMap(value -> value instanceof IAssemblyInstance ? Stream.of(value) : null)
            .collect(Collectors.toList()))
                .when(retval).getAssemblyInstances();
    doReturn(
        modelInstances.values().stream()
            .flatMap(value -> value instanceof IFieldInstance ? Stream.of(value) : null)
            .collect(Collectors.toList()))
                .when(retval).getFieldInstances();
    return retval;
  }

  @Override
  protected void applyNamed(INamedModelElement element) {
    super.applyNamed(element);
    doReturn(ModelType.ASSEMBLY).when(element).getModelType();
  }
}
