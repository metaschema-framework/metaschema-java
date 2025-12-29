/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext.IBindingMatcher;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * A binding matcher that matches based on a root assembly definition.
 * <p>
 * This implementation matches XML elements by their qualified name and
 * JSON/YAML properties by their root name, allowing the binding context to
 * identify the correct bound class for a given document root element.
 */
class RootAssemblyBindingMatcher implements IBindingMatcher {
  @NonNull
  private final IBoundDefinitionModelAssembly definition;
  @NonNull
  private final Lazy<QName> rootQName = ObjectUtils.notNull(
      Lazy.of(() -> getDefinition().getRootQName().toQName()));

  /**
   * Construct a new binding matcher for the provided root assembly definition.
   *
   * @param definition
   *          the root assembly definition to match against
   */
  public RootAssemblyBindingMatcher(
      @NonNull IBoundDefinitionModelAssembly definition) {
    this.definition = definition;
  }

  /**
   * Get the assembly definition this matcher is based on.
   *
   * @return the assembly definition
   */
  protected IBoundDefinitionModelAssembly getDefinition() {
    return definition;
  }

  /**
   * Get the bound class associated with this matcher's definition.
   *
   * @return the bound class
   */
  protected Class<? extends IBoundObject> getClazz() {
    return getDefinition().getBoundClass();
  }

  /**
   * Get the XML qualified name for the root element.
   *
   * @return the root element's QName
   */
  @NonNull
  protected QName getRootQName() {
    return ObjectUtils.notNull(rootQName.get());
  }

  /**
   * Get the JSON/YAML root property name.
   *
   * @return the root JSON name
   */
  @SuppressWarnings("null")
  @NonNull
  protected String getRootJsonName() {
    return getDefinition().getRootJsonName();
  }

  @Override
  public Class<? extends IBoundObject> getBoundClassForXmlQName(QName rootQName) {
    return getRootQName().equals(rootQName) ? getClazz() : null;
  }

  @Override
  public Class<? extends IBoundObject> getBoundClassForJsonName(String rootName) {
    return getRootJsonName().equals(rootName) ? getClazz() : null;
  }

  @Override
  public String toString() {
    return getDefinition().getRootQName().toString();
  }
}
