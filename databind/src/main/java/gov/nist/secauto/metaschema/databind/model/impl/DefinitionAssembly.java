/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IModelConstrained;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelNamed;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;
import gov.nist.secauto.metaschema.databind.model.annotations.AssemblyConstraints;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ModelUtil;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implements a Metaschema module global assembly definition bound to a Java
 * class.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class DefinitionAssembly
    extends AbstractBoundDefinitionModelComplex<MetaschemaAssembly>
    implements IBoundDefinitionModelAssembly,
    IFeatureBoundContainerModelAssembly<
        IBoundInstanceModel<?>,
        IBoundInstanceModelNamed<?>,
        IBoundInstanceModelField<?>,
        IBoundInstanceModelAssembly,
        IBoundInstanceModelChoiceGroup> {

  @NonNull
  private final Lazy<FlagContainerSupport> flagContainer;
  @NonNull
  private final Lazy<AssemblyModelContainerSupport> modelContainer;
  @NonNull
  private final Lazy<IModelConstrained> constraints;
  @NonNull
  private final Lazy<QName> xmlRootQName;
  @NonNull
  private final Lazy<Map<String, IBoundProperty<?>>> jsonProperties;
  @NonNull
  private final Lazy<Map<IAttributable.Key, Set<String>>> properties;

  /**
   * Construct a new global assembly instance.
   *
   * @param clazz
   *          the class the assembly is bound to
   * @param bindingContext
   *          the Metaschema binding context managing this class used to lookup
   *          binding information
   * @return the definition
   */
  @NonNull
  public static DefinitionAssembly newInstance(
      @NonNull Class<? extends IBoundObject> clazz,
      @NonNull MetaschemaAssembly annotation,
      @NonNull IBoundModule module,
      @NonNull IBindingContext bindingContext) {
    return new DefinitionAssembly(clazz, annotation, module, bindingContext);
  }

  private DefinitionAssembly(
      @NonNull Class<? extends IBoundObject> clazz,
      @NonNull MetaschemaAssembly annotation,
      @NonNull IBoundModule module,
      @NonNull IBindingContext bindingContext) {
    super(clazz, annotation, module, bindingContext);

    String rootLocalName = ModelUtil.resolveNoneOrDefault(getAnnotation().rootName(), null);
    this.xmlRootQName = ObjectUtils.notNull(Lazy.lazy(() -> rootLocalName == null
        ? null
        : getContainingModule().toModelQName(rootLocalName)));
    this.flagContainer = ObjectUtils.notNull(Lazy.lazy(() -> new FlagContainerSupport(this, null)));
    this.modelContainer = ObjectUtils.notNull(Lazy.lazy(() -> new AssemblyModelContainerSupport(this)));

    ISource moduleSource = module.getSource();
    this.constraints = ObjectUtils.notNull(Lazy.lazy(() -> {
      IModelConstrained retval = new AssemblyConstraintSet();
      ValueConstraints valueAnnotation = getAnnotation().valueConstraints();
      ConstraintSupport.parse(valueAnnotation, moduleSource, retval);

      AssemblyConstraints assemblyAnnotation = getAnnotation().modelConstraints();
      ConstraintSupport.parse(assemblyAnnotation, moduleSource, retval);
      return retval;
    }));
    this.jsonProperties = ObjectUtils.notNull(Lazy.lazy(() -> getJsonProperties(null)));
    this.properties = ObjectUtils.notNull(
        Lazy.lazy(() -> CollectionUtil.unmodifiableMap(ObjectUtils.notNull(
            Arrays.stream(annotation.properties())
                .map(ModelUtil::toPropertyEntry)
                .collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new))))));
  }

  @Override
  protected void deepCopyItemInternal(IBoundObject fromObject, IBoundObject toObject) throws BindingException {
    // copy the flags
    super.deepCopyItemInternal(fromObject, toObject);

    for (IBoundInstanceModel<?> instance : getModelInstances()) {
      instance.deepCopy(fromObject, toObject);
    }
  }

  @Override
  public Map<String, IBoundProperty<?>> getJsonProperties() {
    return ObjectUtils.notNull(jsonProperties.get());
  }

  // ------------------------------------------
  // - Start annotation driven code - CPD-OFF -
  // ------------------------------------------

  @Override
  @SuppressWarnings("null")
  @NonNull
  public FlagContainerSupport getFlagContainer() {
    return flagContainer.get();
  }

  @Override
  @SuppressWarnings("null")
  @NonNull
  public AssemblyModelContainerSupport getModelContainer() {
    return modelContainer.get();
  }

  @Override
  @NonNull
  public IModelConstrained getConstraintSupport() {
    return ObjectUtils.notNull(constraints.get());
  }

  @Override
  @Nullable
  public String getFormalName() {
    return ModelUtil.resolveNoneOrValue(getAnnotation().formalName());
  }

  @Override
  @Nullable
  public MarkupLine getDescription() {
    return ModelUtil.resolveToMarkupLine(getAnnotation().description());
  }

  @Override
  @NonNull
  public String getName() {
    return getAnnotation().name();
  }

  @Override
  @Nullable
  public Integer getIndex() {
    return ModelUtil.resolveDefaultInteger(getAnnotation().index());
  }

  @Override
  public Map<Key, Set<String>> getProperties() {
    return ObjectUtils.notNull(properties.get());
  }

  @Override
  @Nullable
  public MarkupMultiline getRemarks() {
    return ModelUtil.resolveToMarkupMultiline(getAnnotation().description());
  }

  @Override
  @Nullable
  public QName getRootXmlQName() {
    // Overriding this is more efficient, since it is already built
    return xmlRootQName.get();
  }

  @Override
  public boolean isRoot() {
    // Overriding this is more efficient, since the root name is derived from the
    // XML QName
    return getRootXmlQName() != null;
  }

  @Override
  @Nullable
  public String getRootName() {
    // Overriding this is more efficient, since it is already built
    QName qname = getRootXmlQName();
    return qname == null ? null : qname.getLocalPart();
  }

  @Override
  @Nullable
  public Integer getRootIndex() {
    return ModelUtil.resolveDefaultInteger(getAnnotation().rootIndex());
  }

  // ----------------------------------------
  // - End annotation driven code - CPD-OFF -
  // ----------------------------------------
}
