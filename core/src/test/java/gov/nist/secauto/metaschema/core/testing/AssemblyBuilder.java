/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class AssemblyBuilder
    extends AbstractModelBuilder<AssemblyBuilder>
    implements IModelInstanceBuilder {

  private String rootNamespace = "";
  private String rootName;
  private List<FlagBuilder> flags;
  private List<? extends IModelInstanceBuilder> modelInstances;

  private AssemblyBuilder(@NonNull Mockery ctx) {
    super(ctx);
  }

  /**
   * Create a new builder using the provided mocking context.
   *
   * @param ctx
   *          the mocking context
   * @return the new builder
   */
  @NonNull
  public static AssemblyBuilder builder(@NonNull Mockery ctx) {
    return new AssemblyBuilder(ctx).reset();
  }

  @Override
  public AssemblyBuilder reset() {
    this.flags = CollectionUtil.emptyList();
    this.modelInstances = CollectionUtil.emptyList();
    return this;
  }

  /**
   * Apply the provided root namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @NonNull
  public AssemblyBuilder rootNamespace(@NonNull String name) {
    this.rootNamespace = name;
    return this;
  }

  /**
   * Apply the provided root namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @NonNull
  public AssemblyBuilder rootNamespace(@NonNull URI name) {
    return rootNamespace(ObjectUtils.notNull(name.toASCIIString()));
  }

  /**
   * Apply the provided root name for use by this builder.
   *
   * @param name
   *          the name to use
   * @return this builder
   */
  @NonNull
  public AssemblyBuilder rootName(@NonNull String name) {
    this.rootName = name;
    return this;
  }

  /**
   * Apply the provided root qualified name for use by this builder.
   *
   * @param qname
   *          the qualified name to use
   * @return this builder
   */
  @NonNull
  public AssemblyBuilder rootQName(@NonNull IEnhancedQName qname) {
    this.rootName = qname.getLocalName();
    this.rootNamespace = qname.getNamespace();
    return this;
  }

  /**
   * Use the provided flag instances for built fields.
   *
   * @param flags
   *          the flags to use
   * @return this builder
   */
  public AssemblyBuilder flags(@Nullable List<FlagBuilder> flags) {
    this.flags = flags == null ? CollectionUtil.emptyList() : flags;
    return this;
  }

  /**
   * Use the provided model instances for built fields.
   *
   * @param modelInstances
   *          the model instances to use
   * @return this builder
   */
  public AssemblyBuilder modelInstances(@Nullable List<? extends IModelInstanceBuilder> modelInstances) {
    this.modelInstances = modelInstances == null ? CollectionUtil.emptyList() : modelInstances;
    return this;
  }

  @Override
  @NonNull
  public IAssemblyInstanceAbsolute toInstance(
      @NonNull IAssemblyDefinition parent) {
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
  @NonNull
  public IAssemblyDefinition toDefinition() {
    validate();

    IAssemblyDefinition retval = mock(IAssemblyDefinition.class);
    applyDefinition(retval);

    Map<IEnhancedQName, IFlagInstance> flags = this.flags.stream()
        .map(builder -> builder.toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            IFlagInstance::getQName,
            Function.identity()));

    Map<IEnhancedQName, ? extends INamedModelInstanceAbsolute> modelInstances = this.modelInstances.stream()
        .map(builder -> builder.toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            INamedModelInstanceAbsolute::getQName,
            Function.identity()));

    getContext().checking(new Expectations() {
      {
        if (rootName != null) {
          allowing(retval).getRootQName();
          will(returnValue(IEnhancedQName.of(ObjectUtils.notNull(rootNamespace), ObjectUtils.notNull(rootName))));
        }

        allowing(retval).getFlagInstances();
        will(returnValue(flags.values()));
        flags.forEach((key, value) -> {
          allowing(retval).getFlagInstanceByName(with(key.getIndexPosition()));
          will(returnValue(value));
        });
        allowing(retval).getModelInstances();
        will(returnValue(modelInstances.values()));
        modelInstances.forEach((key, value) -> {
          allowing(retval).getNamedModelInstanceByName(with(key.getIndexPosition()));
          will(returnValue(value));
        });
      }
    });

    return retval;
  }
}
