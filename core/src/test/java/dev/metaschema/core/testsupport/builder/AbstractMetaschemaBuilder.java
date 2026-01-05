/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport.builder;

import static org.mockito.Mockito.doReturn;

import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModelElement;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedInstance;
import dev.metaschema.core.model.INamedModelElement;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.AbstractMockitoFactory;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.core.util.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A base class for Metaschema module-based model builders.
 *
 * @param <T>
 *          the Java type of this builder
 */
abstract class AbstractMetaschemaBuilder<T extends IMetaschemaBuilder<T>>
    extends AbstractMockitoFactory
    implements IMetaschemaBuilder<T> {

  private String namespace = "";
  private String name;
  private ISource source;

  /**
   * Construct a new builder.
   */
  protected AbstractMetaschemaBuilder() {
    // allow extending classes to construct
  }

  @Override
  public T reset() {
    this.name = null;
    this.namespace = null;
    return ObjectUtils.asType(this);
  }

  @Override
  @NonNull
  public T namespace(@NonNull String name) {
    this.namespace = name;
    return ObjectUtils.asType(this);
  }

  @Override
  @NonNull
  public T name(@NonNull String name) {
    this.name = name;
    return ObjectUtils.asType(this);
  }

  @Override
  @NonNull
  public T qname(@NonNull IEnhancedQName qname) {
    this.name = qname.getLocalName();
    this.namespace = qname.getNamespace();
    return ObjectUtils.asType(this);
  }

  @Override
  @NonNull
  public T source(@NonNull ISource source) {
    this.source = source;
    return ObjectUtils.asType(this);
  }

  /**
   * Get the currently configured source.
   *
   * @return the source or {@code null} if no source is configured
   */
  protected ISource getSource() {
    return source;
  }

  /**
   * Get the currently configured namespace.
   *
   * @return the namespace or {@code null} if no namespace is configured
   */
  protected String getNamespace() {
    return namespace;
  }

  /**
   * Get the currently configured name.
   *
   * @return the name or {@code null} if no name is configured
   */
  protected String getName() {
    return name;
  }

  /**
   * Validate the data provided to this builder to ensure correct and required
   * information is provided.
   */
  protected void validate() {
    StringUtils.requireNonEmpty(ObjectUtils.requireNonNull(name), "name");
    ObjectUtils.requireNonNull(source, "source");
  }

  /**
   * Apply expectations to the mocking context for the provided definition.
   *
   * @param definition
   *          the definition to apply mocking expectations for
   */
  protected void applyDefinition(@NonNull IDefinition definition) {
    applyDefinition(definition, null);
  }

  /**
   * Apply expectations to the mocking context for the provided definition.
   *
   * @param definition
   *          the definition to apply mocking expectations for
   * @param module
   *          the containing module, or {@code null} if standalone
   */
  protected void applyDefinition(@NonNull IDefinition definition, @Nullable IModule module) {
    applyModelElement(definition);
    applyNamed(definition);
    applyAttributable(definition);

    IEnhancedQName qname = IEnhancedQName.of(ObjectUtils.notNull(namespace), ObjectUtils.notNull(name));

    doReturn(qname).when(definition).getDefinitionQName();
    doReturn(null).when(definition).getRemarks();
    doReturn(CollectionUtil.emptyMap()).when(definition).getProperties();
    doReturn(null).when(definition).getInlineInstance();

    if (module != null) {
      doReturn(module).when(definition).getContainingModule();
    }

    // doReturn().when(definition).getConstraintSupport();
    // doReturn().when(definition).getModelType();
  }

  /**
   * Apply expectations to the mocking context for the provided instance,
   * definition, and parent definition.
   *
   * @param <DEF>
   *          the Java type of the definition
   * @param instance
   *          the instance to apply mocking expectations for
   * @param definition
   *          the definition to apply mocking expectations for
   * @param parent
   *          the parent definition to apply mocking expectations for
   */
  protected <DEF extends IDefinition> void applyNamedInstance(
      @NonNull INamedInstance instance,
      @NonNull DEF definition,
      @NonNull IModelDefinition parent) {
    applyModelElement(instance);
    applyNamed(instance);
    applyAttributable(instance);

    doReturn(name).when(instance).getName();
    doReturn(definition).when(instance).getDefinition();
    doReturn(parent).when(instance).getContainingDefinition();
    doReturn(parent).when(instance).getParentContainer();
  }

  /**
   * Apply expectations to the mocking context for the provided named model
   * element.
   *
   * @param element
   *          the named model element to apply mocking expectations for
   */
  protected void applyNamed(@NonNull INamedModelElement element) {
    IEnhancedQName qname = IEnhancedQName.of(ObjectUtils.notNull(namespace), ObjectUtils.notNull(name));

    doReturn(qname).when(element).getQName();
    doReturn(name).when(element).getName();
    doReturn(null).when(element).getFormalName();
    doReturn(null).when(element).getDescription();
  }

  /**
   * Apply expectations to the mocking context for the provided attributable
   * element.
   *
   * @param element
   *          the element to apply mocking expectations for
   */
  protected void applyAttributable(@NonNull IAttributable element) {
    doReturn(CollectionUtil.emptyMap()).when(element).getProperties();
  }

  /**
   * Apply expectations to the mocking context for the provided model element.
   *
   * @param element
   *          the model element to apply mocking expectations for
   */
  protected void applyModelElement(@NonNull IModelElement element) {
    doReturn(null).when(element).getRemarks();
  }
}
