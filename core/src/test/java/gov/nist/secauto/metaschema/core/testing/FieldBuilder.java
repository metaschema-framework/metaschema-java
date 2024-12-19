/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelElement;
import gov.nist.secauto.metaschema.core.model.ModelType;
import gov.nist.secauto.metaschema.core.model.constraint.ValueConstraintSet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class FieldBuilder
    extends AbstractModelBuilder<FieldBuilder>
    implements IModelInstanceBuilder {

  private IDataTypeAdapter<?> dataTypeAdapter;
  private Object defaultValue = null;
  private List<FlagBuilder> flags;

  private FieldBuilder() {
    // prevent direct instantiation
  }

  /**
   * Create a new builder using the provided mocking context.
   *
   * @return the new builder
   */
  @NonNull
  public static FieldBuilder builder() {
    return new FieldBuilder().reset();
  }

  @Override
  public FieldBuilder reset() {
    this.dataTypeAdapter = MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    this.defaultValue = null;
    this.flags = CollectionUtil.emptyList();
    return this;
  }

  /**
   * Apply the provided data type adapter to built fields.
   *
   * @param dataTypeAdapter
   *          the data type adapter to use
   * @return this builder
   */
  public FieldBuilder dataTypeAdapter(@NonNull IDataTypeAdapter<?> dataTypeAdapter) {
    this.dataTypeAdapter = dataTypeAdapter;
    return this;
  }

  /**
   * Apply the provided data type adapter to built fields.
   *
   * @param defaultValue
   *          the default value to use
   * @return this builder
   */
  public FieldBuilder defaultValue(@NonNull Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * Use the provided flag instances for built fields.
   *
   * @param flags
   *          the flags to use
   * @return this builder
   */
  public FieldBuilder flags(@Nullable List<FlagBuilder> flags) {
    this.flags = flags == null ? CollectionUtil.emptyList() : flags;
    return this;
  }

  /**
   * Build a mocked field instance, based on a mocked definition, as a child of
   * the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @return the new mocked instance
   */
  @Override
  @NonNull
  public IFieldInstanceAbsolute toInstance(
      @NonNull IAssemblyDefinition parent) {
    IFieldDefinition def = toDefinition();
    return toInstance(parent, def);
  }

  /**
   * Build a mocked field instance, using the provided definition, as a child of
   * the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @param definition
   *          the definition to base the instance on
   * @return the new mocked instance
   */
  @NonNull
  public IFieldInstanceAbsolute toInstance(
      @NonNull IAssemblyDefinition parent,
      @NonNull IFieldDefinition definition) {
    validate();

    IFieldInstanceAbsolute retval = mock(IFieldInstanceAbsolute.class);
    applyNamedInstance(retval, definition, parent);
    return retval;
  }

  /**
   * Build a mocked field definition.
   *
   * @return the new mocked definition
   */
  @NonNull
  public IFieldDefinition toDefinition() {
    validate();

    IFieldDefinition retval = mock(IFieldDefinition.class);
    applyDefinition(retval);

    Map<IEnhancedQName, IFlagInstance> flags = this.flags.stream()
        .map(builder -> builder.toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            IFlagInstance::getQName,
            Function.identity()));

    doReturn(new ValueConstraintSet(source)).when(retval).getConstraintSupport();
    doReturn(dataTypeAdapter).when(retval).getJavaTypeAdapter();
    doReturn(defaultValue).when(retval).getDefaultValue();

    doReturn(flags.values()).when(retval).getFlagInstances();
    flags.entrySet().forEach(entry -> {
      assert entry != null;
      doReturn(entry.getValue()).when(retval).getFlagInstanceByName(eq(entry.getKey().getIndexPosition()));
    });

    return retval;
  }

  @Override
  protected void applyNamed(INamedModelElement element) {
    super.applyNamed(element);
    doReturn(ModelType.FIELD).when(element).getModelType();
  }
}
