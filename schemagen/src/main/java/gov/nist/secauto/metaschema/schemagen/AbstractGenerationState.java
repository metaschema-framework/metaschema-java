/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.datatype.IDatatypeManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides a common base implementation for schema generation state management.
 * <p>
 * This abstract class maintains the context required during schema generation,
 * including the module being processed, the output writer, datatype management,
 * and inlining strategy.
 *
 * @param <WRITER>
 *          the type of writer used for schema output
 * @param <DATATYPE_MANAGER>
 *          the type of datatype manager used for type name resolution
 */
public abstract class AbstractGenerationState<WRITER, DATATYPE_MANAGER extends IDatatypeManager>
    implements IGenerationState<WRITER> {
  @NonNull
  private final IModule module;
  @NonNull
  private final WRITER writer;
  @NonNull
  private final DATATYPE_MANAGER datatypeManager;
  @NonNull
  private final IInlineStrategy inlineStrategy;

  @NonNull
  private final ModuleIndex moduleIndex;

  /**
   * Construct a new generation state instance.
   *
   * @param module
   *          the Metaschema module to generate a schema for
   * @param writer
   *          the output writer for the generated schema
   * @param configuration
   *          the configuration controlling schema generation behavior
   * @param datatypeManager
   *          the manager for handling datatype name resolution
   */
  public AbstractGenerationState(
      @NonNull IModule module,
      @NonNull WRITER writer,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration,
      @NonNull DATATYPE_MANAGER datatypeManager) {
    this.module = module;
    this.writer = writer;
    this.datatypeManager = datatypeManager;
    this.inlineStrategy = IInlineStrategy.newInlineStrategy(configuration);
    this.moduleIndex = ModuleIndex.indexDefinitions(module, this.inlineStrategy);
  }

  @Override
  public IModule getModule() {
    return module;
  }

  @Override
  public WRITER getWriter() {
    return writer;
  }

  /**
   * Get the datatype manager used for type name resolution.
   *
   * @return the datatype manager
   */
  @NonNull
  protected DATATYPE_MANAGER getDatatypeManager() {
    return datatypeManager;
  }

  /**
   * Get the module index containing indexed definitions from the module.
   *
   * @return the module index
   */
  @NonNull
  public ModuleIndex getMetaschemaIndex() {
    return moduleIndex;
  }

  @Override
  public boolean isInline(@NonNull IDefinition definition) {
    return inlineStrategy.isInline(definition, getMetaschemaIndex());
  }

  /**
   * Retrieve any allowed values that are context independent, meaning they always
   * apply regardless of the location of the node in the larger graph.
   *
   * @param definition
   *          the definition to get allowed values for
   * @return the list of allowed values or an empty list
   */
  @NonNull
  protected static AllowedValueCollection getContextIndependentEnumeratedValues(
      @NonNull IValuedDefinition definition) {
    List<IAllowedValue> values = new LinkedList<>();
    boolean closed = false;
    for (IAllowedValuesConstraint constraint : definition.getAllowedValuesConstraints()) {
      assert constraint != null;
      if (!constraint.isAllowedOther()) {
        closed = true;
      }

      // FIXME: Should this compare the actual compiled expression?
      if (!IMetapathExpression.contextNode().getPath().equals(constraint.getTarget().getPath())) {
        values = CollectionUtil.emptyList();
        break;
      }

      values.addAll(constraint.getAllowedValues().values());
    }
    return new AllowedValueCollection(closed, values);
  }

  /**
   * Get the name of the definition (and any parent instances/definition) to
   * ensure an inline type is unique.
   *
   * @param definition
   *          the definition to generate a type name for
   * @param childModule
   *          the module of the left node
   * @return the unique type name
   */
  private CharSequence getTypeContext(
      @NonNull IDefinition definition,
      @NonNull IModule childModule) {
    StringBuilder builder = new StringBuilder();
    if (definition.isInline()) {
      INamedInstance inlineInstance = definition.getInlineInstance();
      IDefinition parentDefinition = inlineInstance.getContainingDefinition();

      builder
          .append(getTypeContext(parentDefinition, childModule))
          .append(IGenerationState.toCamelCase(inlineInstance.getEffectiveName()));
    } else {
      builder.append(IGenerationState.toCamelCase(definition.getName()));
    }
    return builder;
  }

  @Override
  @NonNull
  public String getTypeNameForDefinition(@NonNull IDefinition definition, @Nullable String suffix) {
    StringBuilder builder = new StringBuilder()
        .append(IGenerationState.toCamelCase(definition.getModelType().name()))
        .append(IGenerationState.toCamelCase(definition.getContainingModule().getShortName()));

    if (isInline(definition)) {
      builder.append(IGenerationState.toCamelCase(definition.getEffectiveName()));
    } else {
      // need to append the parent name(s) to disambiguate this type name
      builder.append(getTypeContext(definition, definition.getContainingModule()));
    }
    if (suffix != null && !suffix.isBlank()) {
      builder.append(suffix);
    }
    builder.append("Type");

    return ObjectUtils.notNull(builder.toString());
  }

  /**
   * Represents a collection of allowed values with a flag indicating whether the
   * value set is closed (no other values allowed) or open.
   */
  public static class AllowedValueCollection {
    private final boolean closed;
    @NonNull
    private final List<IAllowedValue> values;

    /**
     * Construct a new allowed value collection.
     *
     * @param closed
     *          {@code true} if only the specified values are allowed, {@code false}
     *          if other values are also permitted
     * @param values
     *          the list of allowed values
     */
    public AllowedValueCollection(boolean closed, @NonNull List<IAllowedValue> values) {
      this.closed = closed;
      this.values = CollectionUtil.unmodifiableList(new ArrayList<>(values));
    }

    /**
     * Determine if the allowed value set is closed.
     *
     * @return {@code true} if only the specified values are allowed, {@code false}
     *         if other values are also permitted
     */
    public boolean isClosed() {
      return closed;
    }

    /**
     * Get the list of allowed values.
     *
     * @return an unmodifiable list of allowed values
     */
    @NonNull
    public List<IAllowedValue> getValues() {
      return values;
    }
  }
}
