/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.jmock.Expectations;
import org.jmock.Mockery;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class FieldBuilder
    extends AbstractModelBuilder<FieldBuilder>
    implements IModelInstanceBuilder {

  private IDataTypeAdapter<?> dataTypeAdapter;
  private Object defaultValue = null;
  private List<FlagBuilder> flags;

  private FieldBuilder(@NonNull Mockery ctx) {
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
  public static FieldBuilder builder(@NonNull Mockery ctx) {
    return new FieldBuilder(ctx).reset();
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
  @SuppressWarnings("null")
  @NonNull
  public IFieldDefinition toDefinition() {
    validate();

    IFieldDefinition retval = mock(IFieldDefinition.class);
    applyDefinition(retval);

    Map<QName, IFlagInstance> flags = this.flags.stream()
        .map(builder -> builder.toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            IFlagInstance::getXmlQName,
            Function.identity()));

    getContext().checking(new Expectations() {
      {
        allowing(retval).getJavaTypeAdapter();
        will(returnValue(dataTypeAdapter));
        allowing(retval).getDefaultValue();
        will(returnValue(defaultValue));
        allowing(retval).getFlagInstances();
        will(returnValue(flags.values()));
        flags.forEach((key, value) -> {
          allowing(retval).getFlagInstanceByName(with(key));
          will(returnValue(value));
        });
      }
    });

    return retval;
  }
}
