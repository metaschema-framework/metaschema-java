/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;

import org.jmock.Expectations;
import org.jmock.Mockery;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A builder that generates mock flag definitions and instances.
 */
public final class FlagBuilder
    extends AbstractModelBuilder<FlagBuilder> {

  private IDataTypeAdapter<?> dataTypeAdapter;
  private Object defaultValue = null;
  private boolean required;

  private FlagBuilder(@NonNull Mockery ctx) {
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
  public static FlagBuilder builder(@NonNull Mockery ctx) {
    return new FlagBuilder(ctx).reset();
  }

  @Override
  public FlagBuilder reset() {
    this.dataTypeAdapter = MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    this.defaultValue = null;
    this.required = IFlagInstance.DEFAULT_FLAG_REQUIRED;
    return this;
  }

  /**
   * Apply the provided required setting to built flags.
   *
   * @param required
   *          {@code true} if the flag is required or {@code false} otherwise
   * @return this builder
   */
  public FlagBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  /**
   * Apply the provided data type adapter to built flags.
   *
   * @param dataTypeAdapter
   *          the data type adapter to use
   * @return this builder
   */
  public FlagBuilder dataTypeAdapter(@NonNull IDataTypeAdapter<?> dataTypeAdapter) {
    this.dataTypeAdapter = dataTypeAdapter;
    return this;
  }

  /**
   * Apply the provided data type adapter to built flags.
   *
   * @param defaultValue
   *          the default value to use
   * @return this builder
   */
  public FlagBuilder defaultValue(@NonNull Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * Build a mocked flag instance, based on a mocked definition, as a child of the
   * provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @return the new mocked instance
   */
  @NonNull
  public IFlagInstance toInstance(@NonNull IModelDefinition parent) {
    IFlagDefinition def = toDefinition();
    return toInstance(parent, def);
  }

  /**
   * Build a mocked flag instance, using the provided definition, as a child of
   * the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @param definition
   *          the definition to base the instance on
   * @return the new mocked instance
   */
  @NonNull
  public IFlagInstance toInstance(
      @NonNull IModelDefinition parent,
      @NonNull IFlagDefinition definition) {
    validate();

    IFlagInstance retval = mock(IFlagInstance.class);

    applyNamedInstance(retval, definition, parent);

    getContext().checking(new Expectations() {
      {
        allowing(retval).isRequired();
        will(returnValue(required));
      }
    });

    return retval;
  }

  /**
   * Build a mocked flag definition.
   *
   * @return the new mocked definition
   */
  @NonNull
  public IFlagDefinition toDefinition() {
    validate();

    IFlagDefinition retval = mock(IFlagDefinition.class);
    applyDefinition(retval);

    getContext().checking(new Expectations() {
      {
        allowing(retval).getJavaTypeAdapter();
        will(returnValue(dataTypeAdapter));
        allowing(retval).getDefaultValue();
        will(returnValue(defaultValue));
      }
    });
    return retval;
  }
}
