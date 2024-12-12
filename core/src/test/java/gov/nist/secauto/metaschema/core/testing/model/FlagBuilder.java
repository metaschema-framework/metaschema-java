/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelElement;
import gov.nist.secauto.metaschema.core.model.ModelType;
import gov.nist.secauto.metaschema.core.model.constraint.ValueConstraintSet;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A builder that generates mock flag definitions and instances.
 */
final class FlagBuilder
    extends AbstractMetaschemaBuilder<IFlagBuilder>
    implements IFlagBuilder {

  private IDataTypeAdapter<?> dataTypeAdapter;
  private Object defaultValue = null;
  private boolean required;

  FlagBuilder() {
    // prevent direct instantiation
  }

  @Override
  public FlagBuilder reset() {
    this.dataTypeAdapter = MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    this.defaultValue = null;
    this.required = IFlagInstance.DEFAULT_FLAG_REQUIRED;
    return this;
  }

  @Override
  public FlagBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  @Override
  public FlagBuilder dataTypeAdapter(@NonNull IDataTypeAdapter<?> dataTypeAdapter) {
    this.dataTypeAdapter = dataTypeAdapter;
    return this;
  }

  @Override
  public FlagBuilder defaultValue(@NonNull Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  @Override
  @NonNull
  public IFlagInstance toInstance(
      @NonNull IModelDefinition parent,
      @NonNull IFlagDefinition definition) {
    validate();

    IFlagInstance retval = mock(IFlagInstance.class);

    applyNamedInstance(retval, definition, parent);

    doReturn(required).when(retval).isRequired();
    return retval;
  }

  /**
   * Build a mocked flag definition.
   *
   * @return the new mocked definition
   */
  @Override
  @NonNull
  public IFlagDefinition toDefinition() {
    validate();

    IFlagDefinition retval = mock(IFlagDefinition.class);
    applyDefinition(retval);

    doReturn(new ValueConstraintSet(ObjectUtils.notNull(getSource()))).when(retval).getConstraintSupport();
    doReturn(dataTypeAdapter).when(retval).getJavaTypeAdapter();
    doReturn(defaultValue).when(retval).getDefaultValue();

    return retval;
  }

  @Override
  protected void applyNamed(INamedModelElement element) {
    super.applyNamed(element);
    doReturn(ModelType.FLAG).when(element).getModelType();
  }
}
