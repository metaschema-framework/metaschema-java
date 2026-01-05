/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.io.BindingException;
import dev.metaschema.databind.model.IBoundDefinitionModelComplex;
import dev.metaschema.databind.model.IBoundInstanceFlag;
import dev.metaschema.databind.model.IBoundModule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * An abstract base implementation of a complex model definition bound to a Java
 * class.
 * <p>
 * This class provides the common implementation for field and assembly
 * definitions that are bound to Java classes through annotations.
 *
 * @param <A>
 *          the annotation type used to configure the definition
 */
public abstract class AbstractBoundDefinitionModelComplex<A extends Annotation>
    implements IBoundDefinitionModelComplex {
  @NonNull
  private final Class<? extends IBoundObject> clazz;
  @NonNull
  private final A annotation;
  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final IBoundModule module;
  @NonNull
  private final Lazy<IEnhancedQName> qname;
  @NonNull
  private final Lazy<IEnhancedQName> definitionQName;
  @Nullable
  private final Method beforeDeserializeMethod;
  @Nullable
  private final Method afterDeserializeMethod;

  /**
   * Constructs a new complex model definition bound to a Java class.
   *
   * @param clazz
   *          the Java class bound to this definition
   * @param annotation
   *          the binding annotation on the class
   * @param module
   *          the module containing this definition
   * @param bindingContext
   *          the binding context used for resolving definitions
   */
  protected AbstractBoundDefinitionModelComplex(
      @NonNull Class<? extends IBoundObject> clazz,
      @NonNull A annotation,
      @NonNull IBoundModule module,
      @NonNull IBindingContext bindingContext) {
    this.clazz = clazz;
    this.annotation = annotation;
    this.bindingContext = bindingContext;
    this.module = module;
    this.qname = ObjectUtils.notNull(Lazy.of(() -> ModuleUtils.parseModelName(
        getContainingModule(),
        getEffectiveName())));
    this.definitionQName = ObjectUtils.notNull(Lazy.of(() -> ModuleUtils.parseModelName(
        getContainingModule(),
        getName())));
    this.beforeDeserializeMethod = ClassIntrospector.getMatchingMethod(
        clazz,
        "beforeDeserialize",
        Object.class);
    this.afterDeserializeMethod = ClassIntrospector.getMatchingMethod(
        clazz,
        "afterDeserialize",
        Object.class);
  }

  @Override
  public Class<? extends IBoundObject> getBoundClass() {
    return clazz;
  }

  /**
   * Gets the binding annotation associated with this definition.
   *
   * @return the annotation used to configure this definition
   */
  @NonNull
  public A getAnnotation() {
    return annotation;
  }

  @Override
  @NonNull
  public IBoundModule getContainingModule() {
    return module;
  }

  @Override
  public ISource getSource() {
    return getContainingModule().getSource();
  }

  @Override
  @NonNull
  public IBindingContext getBindingContext() {
    return bindingContext;
  }

  @SuppressWarnings("null")
  @Override
  public final IEnhancedQName getQName() {
    return qname.get();
  }

  @SuppressWarnings("null")
  @Override
  public final IEnhancedQName getDefinitionQName() {
    return definitionQName.get();
  }

  @Override
  public Method getBeforeDeserializeMethod() {
    return beforeDeserializeMethod;
  }

  @Override
  public Method getAfterDeserializeMethod() {
    return afterDeserializeMethod;
  }

  // @Override
  // public String getJsonKeyFlagName() {
  // // definition items never have a JSON key
  // return null;
  // }

  @Override
  public IBoundObject deepCopyItem(IBoundObject item, IBoundObject parentInstance) throws BindingException {
    IBoundObject instance = newInstance(item::getMetaschemaData);

    callBeforeDeserialize(instance, parentInstance);

    deepCopyItemInternal(item, instance);

    callAfterDeserialize(instance, parentInstance);

    return instance;
  }

  /**
   * Performs the internal deep copy of data from one bound object to another.
   * <p>
   * This implementation copies all flag instances. Subclasses should override
   * this method to copy additional model content.
   *
   * @param fromObject
   *          the source object to copy from
   * @param toObject
   *          the target object to copy to
   * @throws BindingException
   *           if an error occurs during the copy operation
   */
  protected void deepCopyItemInternal(@NonNull IBoundObject fromObject, @NonNull IBoundObject toObject)
      throws BindingException {
    for (IBoundInstanceFlag instance : getFlagInstances()) {
      assert instance != null;

      instance.deepCopy(fromObject, toObject);
    }
  }
}
