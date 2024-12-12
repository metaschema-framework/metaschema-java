/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElement;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelElement;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.mocking.AbstractMockitoFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for Metaschema module-based model builders.
 *
 * @param <T>
 *          the Java type of this builder
 */
public abstract class AbstractModelBuilder<T extends AbstractModelBuilder<T>>
    extends AbstractMockitoFactory {

  private String namespace = "";
  private String name;
  protected ISource source;

  /**
   * Construct a new builder.
   */
  protected AbstractModelBuilder() {
    // allow extending classes to construct
  }

  /**
   * Reset the builder back to a default state.
   *
   * @return this builder
   */
  @NonNull
  @SuppressWarnings("unchecked")
  public T reset() {
    this.name = null;
    this.namespace = null;
    return (T) this;
  }

  /**
   * Apply the provided namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public T namespace(@NonNull String name) {
    this.namespace = name;
    return (T) this;
  }

  /**
   * Apply the provided namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public T namespace(@NonNull URI name) {
    return namespace(name.toASCIIString());
  }

  /**
   * Apply the provided name for use by this builder.
   *
   * @param name
   *          the name to use
   * @return this builder
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public T name(@NonNull String name) {
    this.name = name;
    return (T) this;
  }

  /**
   * Apply the provided qualified name for use by this builder.
   *
   * @param qname
   *          the qualified name to use
   * @return this builder
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public T qname(@NonNull IEnhancedQName qname) {
    this.name = qname.getLocalName();
    this.namespace = qname.getNamespace();
    return (T) this;
  }

  /**
   * Validate the data provided to this builder to ensure correct and required
   * information is provided.
   */
  protected void validate() {
    ObjectUtils.requireNonEmpty(name, "name");
  }

  /**
   * Apply expectations to the mocking context for the provided definition.
   *
   * @param definition
   *          the definition to apply mocking expectations for
   */
  protected void applyDefinition(@NonNull IDefinition definition) {
    applyModelElement(definition);
    applyNamed(definition);
    applyAttributable(definition);

    IEnhancedQName qname = IEnhancedQName.of(ObjectUtils.notNull(namespace), ObjectUtils.notNull(name));

    doReturn(qname).when(definition).getDefinitionQName();
    doReturn(null).when(definition).getRemarks();
    doReturn(CollectionUtil.emptyMap()).when(definition).getProperties();
    doReturn(null).when(definition).getInlineInstance();

    // doReturn().when(definition).getConstraintSupport();
    // doReturn().when(definition).getContainingModule();
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
