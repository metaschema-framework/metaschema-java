/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.model.AbstractLoader;
import dev.metaschema.core.model.IConstraintLoader;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.constraint.AssemblyConstraintSet;
import dev.metaschema.core.model.constraint.AssemblyTargetedConstraints;
import dev.metaschema.core.model.constraint.DefaultScopedContraints;
import dev.metaschema.core.model.constraint.FieldTargetedConstraints;
import dev.metaschema.core.model.constraint.FlagTargetedConstraints;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.core.model.constraint.IModelConstrained;
import dev.metaschema.core.model.constraint.IScopedContraints;
import dev.metaschema.core.model.constraint.ITargetedConstraints;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.model.constraint.MetaConstraintSet;
import dev.metaschema.core.model.constraint.ScopedConstraintSet;
import dev.metaschema.core.model.constraint.ValueConstraintSet;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.io.IBoundLoader;
import dev.metaschema.databind.model.metaschema.binding.AssemblyConstraints;
import dev.metaschema.databind.model.metaschema.binding.MetapathContext;
import dev.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints;
import dev.metaschema.databind.model.metaschema.binding.MetaschemaMetapath;
import dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints;
import dev.metaschema.databind.model.metaschema.impl.ConstraintBindingSupport;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides methods to load a constraint set expressed in any supported
 * Metaschema format.
 * <p>
 * Loaded constraint instances are cached to avoid the need to load them for
 * every use. Any constraint set imported is also loaded and cached
 * automatically.
 */
/**
 * Loads Metaschema constraints from external constraint files using data
 * binding.
 * <p>
 * This class provides functionality to parse constraint files and apply them to
 * Metaschema modules.
 */
public class BindingConstraintLoader
    extends AbstractLoader<List<IConstraintSet>>
    implements IConstraintLoader {

  @NonNull
  private final IBoundLoader loader;

  /**
   * Construct a new loader.
   *
   * @param bindingContext
   *          the Metaschema binding information used to parse constraint data
   */
  public BindingConstraintLoader(@NonNull IBindingContext bindingContext) {
    this.loader = bindingContext.newBoundLoader();
    // this.loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
  }

  @Override
  protected List<IConstraintSet> parseResource(@NonNull URI resource, @NonNull Deque<URI> visitedResources)
      throws IOException {
    StaticContext.Builder builder = StaticContext.builder()
        .baseUri(resource);
    builder.useWildcardWhenNamespaceNotDefaulted(true);

    // parse this set of constraints
    Object constraintsDocument = loader.load(resource);

    List<IConstraintSet> retval;
    if (constraintsDocument instanceof MetaschemaModuleConstraints) {
      MetaschemaModuleConstraints obj = (MetaschemaModuleConstraints) constraintsDocument;

      // now check if this constraint set imports other constraint sets
      List<MetaschemaModuleConstraints.Import> imports = CollectionUtil.listOrEmpty(obj.getImports());

      // handle imports
      @NonNull
      Set<IConstraintSet> importedConstraints;
      if (imports.isEmpty()) {
        importedConstraints = CollectionUtil.emptySet();
      } else {
        try {
          importedConstraints = new LinkedHashSet<>();
          for (MetaschemaModuleConstraints.Import imported : imports) {
            URI importedResource = imported.getHref();
            importedResource = ObjectUtils.notNull(resource.resolve(importedResource));
            importedConstraints.addAll(loadInternal(importedResource, visitedResources));
          }
        } catch (MetaschemaException ex) {
          throw new IOException(ex);
        }
      }

      // handle namespace to prefix bindings
      CollectionUtil.listOrEmpty(obj.getNamespaceBindings()).stream()
          .forEach(binding -> builder.namespace(
              ObjectUtils.notNull(binding.getPrefix()),
              ObjectUtils.notNull(binding.getUri())));
      ISource source = ISource.externalSource(builder.build(), false);

      // create the constraint set
      retval = CollectionUtil.singletonList(new ScopedConstraintSet(
          source,
          parseScopedConstraints(obj, source),
          new LinkedHashSet<>(importedConstraints)));
    } else if (constraintsDocument instanceof MetaschemaMetaConstraints) {
      MetaschemaMetaConstraints obj = (MetaschemaMetaConstraints) constraintsDocument;

      // now check if this constraint set imports other constraint sets
      List<MetaschemaMetaConstraints.Import> imports = CollectionUtil.listOrEmpty(obj.getImports());

      List<IConstraintSet> importedConstraints = new LinkedList<>();
      if (!imports.isEmpty()) {
        try {
          for (MetaschemaMetaConstraints.Import imported : imports) {
            URI importedResource = imported.getHref();
            importedResource = ObjectUtils.notNull(resource.resolve(importedResource));
            importedConstraints.addAll(loadInternal(importedResource, visitedResources));
          }
        } catch (MetaschemaException ex) {
          throw new IOException(ex);
        }
      }

      CollectionUtil.listOrEmpty(obj.getNamespaceBindings()).stream()
          .forEach(binding -> builder.namespace(
              ObjectUtils.notNull(binding.getPrefix()),
              ObjectUtils.notNull(binding.getUri())));

      ISource source = ISource.externalSource(builder.build(), false);

      List<MetaConstraintSet.Context> contexts
          = ObjectUtils.notNull(CollectionUtil.listOrEmpty(obj.getContexts()).stream()
              .map(context -> parseContext(ObjectUtils.notNull(context), null, source))
              .collect(Collectors.toUnmodifiableList()));
      retval = Collections.singletonList(new MetaConstraintSet(source, importedConstraints, contexts));
    } else {
      throw new UnsupportedOperationException(String.format("Unsupported constraint content '%s'.", resource));
    }
    return retval;
  }

  /**
   * Parse individual constraint definitions from the provided bound constraint
   * document.
   *
   * @param obj
   *          the bound constraint document
   * @param source
   *          the source of the constraint content
   * @return the scoped constraint definitions
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // intentional
  @NonNull
  protected List<IScopedContraints> parseScopedConstraints(
      @NonNull MetaschemaModuleConstraints obj,
      @NonNull ISource source) {
    List<IScopedContraints> scopedConstraints = new LinkedList<>();

    for (MetaschemaModuleConstraints.Scope scope : CollectionUtil.listOrEmpty(obj.getScopes())) {
      assert scope != null;

      List<ITargetedConstraints> targetedConstraints = new LinkedList<>();
      for (IValueConstraintsBase constraintsObj : CollectionUtil.listOrEmpty(scope.getConstraints())) {
        if (constraintsObj instanceof MetaschemaModuleConstraints.Scope.Assembly) {
          targetedConstraints.add(handleScopedAssembly(
              (MetaschemaModuleConstraints.Scope.Assembly) constraintsObj,
              source));
        } else if (constraintsObj instanceof MetaschemaModuleConstraints.Scope.Field) {
          targetedConstraints.add(handleScopedField(
              (MetaschemaModuleConstraints.Scope.Field) constraintsObj,
              source));
        } else if (constraintsObj instanceof MetaschemaModuleConstraints.Scope.Flag) {
          targetedConstraints.add(handleScopedFlag(
              (MetaschemaModuleConstraints.Scope.Flag) constraintsObj,
              source));
        }
      }

      URI namespace = ObjectUtils.requireNonNull(scope.getMetaschemaNamespace());
      String shortName = ObjectUtils.requireNonNull(scope.getMetaschemaShortName());

      scopedConstraints.add(new DefaultScopedContraints(
          namespace,
          shortName,
          CollectionUtil.unmodifiableList(targetedConstraints)));
    }
    return CollectionUtil.unmodifiableList(scopedConstraints);
  }

  private static AssemblyTargetedConstraints handleScopedAssembly(
      @NonNull MetaschemaModuleConstraints.Scope.Assembly obj,
      @NonNull ISource source) {
    IModelConstrained constraints = new AssemblyConstraintSet(source);
    ConstraintBindingSupport.parse(constraints, obj, source);
    return new AssemblyTargetedConstraints(
        source,
        () -> Collections.singletonList(IMetapathExpression.lazyCompile(
            ObjectUtils.requireNonNull(obj.getTarget()),
            source.getStaticContext())),
        constraints);
  }

  private static FieldTargetedConstraints handleScopedField(
      @NonNull MetaschemaModuleConstraints.Scope.Field obj,
      @NonNull ISource source) {
    IValueConstrained constraints = new ValueConstraintSet(source);
    ConstraintBindingSupport.parse(constraints, obj, source);

    return new FieldTargetedConstraints(
        source,
        () -> Collections.singletonList(IMetapathExpression.lazyCompile(
            ObjectUtils.requireNonNull(obj.getTarget()),
            source.getStaticContext())),
        constraints);
  }

  private static FlagTargetedConstraints handleScopedFlag(
      @NonNull MetaschemaModuleConstraints.Scope.Flag obj,
      @NonNull ISource source) {
    IValueConstrained constraints = new ValueConstraintSet(source);
    ConstraintBindingSupport.parse(constraints, obj, source);

    return new FlagTargetedConstraints(
        source,
        () -> Collections.singletonList(IMetapathExpression.lazyCompile(
            ObjectUtils.requireNonNull(obj.getTarget()),
            source.getStaticContext())),
        constraints);
  }

  private MetaConstraintSet.Context parseContext(
      @NonNull MetapathContext contextObj,
      @Nullable MetaConstraintSet.Context parent,
      @NonNull ISource source) {

    // generate the metapaths
    List<IMetapathExpression> metapaths = ObjectUtils
        .notNull(CollectionUtil.listOrEmpty(contextObj.getMetapaths()).stream()
            .map(MetaschemaMetapath::getTarget)
            .map(metapath -> IMetapathExpression.lazyCompile(
                ObjectUtils.requireNonNull(metapath),
                source.getStaticContext()))
            .collect(Collectors.toUnmodifiableList()));

    // parse the constraints
    AssemblyConstraints contextConstraints = contextObj.getConstraints();
    IModelConstrained constraints = new AssemblyConstraintSet(source);
    if (contextConstraints != null) {
      ConstraintBindingSupport.parse(constraints, contextConstraints, source);
    }

    // create the context
    MetaConstraintSet.Context context = new MetaConstraintSet.Context(parent, source, metapaths, constraints);

    // create the child contexts
    List<MetaConstraintSet.Context> childContexts
        = ObjectUtils.notNull(CollectionUtil.listOrEmpty(contextObj.getContexts()).stream()
            .map(childObj -> parseContext(ObjectUtils.notNull(childObj), context, source))
            .collect(Collectors.toList()));

    context.addAll(childContexts);

    return context;
  }
}
