/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.AbstractLoader;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.constraint.AbstractTargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IFeatureModelConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.IModelConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ITargetedConstraints;
import gov.nist.secauto.metaschema.core.model.xml.impl.ConstraintXmlSupport;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.MetaschemaMetaConstraintsDocument;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.MetaschemaMetapathReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.ModelContextType;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@SuppressWarnings("PMD.CouplingBetweenObjects")
public class XmlMetaConstraintLoader
    extends AbstractLoader<List<IConstraintSet>>
    implements IConstraintLoader {

  @Override
  protected List<IConstraintSet> parseResource(URI resource, Deque<URI> visitedResources) throws IOException {

    // parse this metaschema
    MetaschemaMetaConstraintsDocument xmlObject = parseConstraintSet(resource);

    MetaschemaMetaConstraintsDocument.MetaschemaMetaConstraints constraints = xmlObject.getMetaschemaMetaConstraints();

    StaticContext.Builder builder = StaticContext.builder()
        .baseUri(resource);

    constraints.getNamespaceBindingList().stream()
        .forEach(binding -> builder.namespace(
            ObjectUtils.notNull(binding.getPrefix()), ObjectUtils.notNull(binding.getUri())));
    builder.useWildcardWhenNamespaceNotDefaulted(true);

    ISource source = ISource.externalSource(builder.build());

    List<ITargetedConstraints> targetedConstraints = ObjectUtils.notNull(constraints.getContextList().stream()
        .flatMap(context -> parseContext(ObjectUtils.notNull(context), null, source).getTargetedConstraints().stream())
        .collect(Collectors.toList()));
    return CollectionUtil.singletonList(new MetaConstraintSet(targetedConstraints));
  }

  private Context parseContext(
      @NonNull ModelContextType contextObj,
      @Nullable Context parent,
      @NonNull ISource source) {

    List<String> metapaths;
    if (parent == null) {
      metapaths = ObjectUtils.notNull(contextObj.getMetapathList().stream()
          .map(MetaschemaMetapathReferenceType::getTarget)
          .collect(Collectors.toList()));
    } else {
      List<String> parentMetapaths = parent.getMetapaths().stream()
          .collect(Collectors.toList());
      metapaths = ObjectUtils.notNull(contextObj.getMetapathList().stream()
          .map(MetaschemaMetapathReferenceType::getTarget)
          .flatMap(childPath -> {
            return parentMetapaths.stream()
                .map(parentPath -> parentPath + '/' + childPath);
          })
          .collect(Collectors.toList()));
    }

    IModelConstrained constraints = new AssemblyConstraintSet();
    ConstraintXmlSupport.parse(constraints, ObjectUtils.notNull(contextObj.getConstraints()), source);
    Context context = new Context(metapaths, constraints);

    List<Context> childContexts = contextObj.getContextList().stream()
        .map(childObj -> parseContext(ObjectUtils.notNull(childObj), context, source))
        .collect(Collectors.toList());

    context.addAll(childContexts);

    return context;
  }

  /**
   * Parse the provided XML resource as a Metaschema constraints.
   *
   * @param resource
   *          the resource to parse
   * @return the XMLBeans representation of the Metaschema contraints
   * @throws IOException
   *           if a parsing error occurred
   */
  @NonNull
  protected MetaschemaMetaConstraintsDocument parseConstraintSet(@NonNull URI resource) throws IOException {
    try {
      XmlOptions options = new XmlOptions();
      options.setBaseURI(resource);
      options.setLoadLineNumbers();
      return ObjectUtils.notNull(MetaschemaMetaConstraintsDocument.Factory.parse(resource.toURL(),
          options));
    } catch (XmlException ex) {
      throw new IOException(ex);
    }
  }

  private static class Context {
    @NonNull
    private final List<String> metapaths;
    @NonNull
    private final IModelConstrained constraints;
    @NonNull
    private final List<Context> childContexts = new LinkedList<>();

    public Context(
        @NonNull List<String> metapaths,
        @NonNull IModelConstrained constraints) {
      this.metapaths = metapaths;
      this.constraints = constraints;
    }

    public List<ITargetedConstraints> getTargetedConstraints() {
      return Stream.concat(
          getMetapaths().stream()
              .map(metapath -> new MetaTargetedContraints(ObjectUtils.notNull(metapath), constraints)),
          childContexts.stream()
              .flatMap(child -> child.getTargetedConstraints().stream()))
          .collect(Collectors.toList());
    }

    public void addAll(Collection<Context> childContexts) {
      childContexts.addAll(childContexts);
    }

    public List<String> getMetapaths() {
      return metapaths;
    }
  }

  private static class MetaTargetedContraints
      extends AbstractTargetedConstraints<IModelConstrained>
      implements IFeatureModelConstrained {

    protected MetaTargetedContraints(
        @NonNull String target,
        @NonNull IModelConstrained constraints) {
      super(target, constraints);
    }

    /**
     * Apply the constraints to the provided {@code definition}.
     * <p>
     * This will be called when a definition is found that matches the target
     * expression.
     *
     * @param definition
     *          the definition to apply the constraints to.
     */
    protected void applyTo(@NonNull IDefinition definition) {
      getAllowedValuesConstraints().forEach(definition::addConstraint);
      getMatchesConstraints().forEach(definition::addConstraint);
      getIndexHasKeyConstraints().forEach(definition::addConstraint);
      getExpectConstraints().forEach(definition::addConstraint);
    }

    protected void applyTo(@NonNull IAssemblyDefinition definition) {
      applyTo((IDefinition) definition);
      getIndexConstraints().forEach(definition::addConstraint);
      getUniqueConstraints().forEach(definition::addConstraint);
      getHasCardinalityConstraints().forEach(definition::addConstraint);
    }

    @Override
    public void target(IFlagDefinition definition) {
      applyTo(definition);
    }

    @Override
    public void target(IFieldDefinition definition) {
      applyTo(definition);
    }

    @Override
    public void target(IAssemblyDefinition definition) {
      applyTo(definition);
    }
  }

  private static final class MetaConstraintSet implements IConstraintSet {
    @NonNull
    private final List<ITargetedConstraints> targetedConstraints;

    private MetaConstraintSet(@NonNull List<ITargetedConstraints> targetedConstraints) {
      this.targetedConstraints = targetedConstraints;
    }

    @Override
    public Iterable<ITargetedConstraints> getTargetedConstraintsForModule(IModule module) {
      return targetedConstraints;
    }

    @Override
    public Collection<IConstraintSet> getImportedConstraintSets() {
      return CollectionUtil.emptyList();
    }

  }
}
