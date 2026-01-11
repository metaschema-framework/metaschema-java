/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport.builder;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedModelElement;
import dev.metaschema.core.model.ModelType;
import dev.metaschema.core.model.constraint.ValueConstraintSet;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

final class FieldBuilder
    extends AbstractModelBuilder<IFieldBuilder>
    implements IFieldBuilder {

  private IDataTypeAdapter<?> dataTypeAdapter;
  private Object defaultValue = null;

  FieldBuilder() {
    // prevent direct instantiation
  }

  @Override
  public FieldBuilder reset() {
    this.dataTypeAdapter = MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    this.defaultValue = null;
    return this;
  }

  @Override
  public FieldBuilder dataTypeAdapter(@NonNull IDataTypeAdapter<?> dataTypeAdapter) {
    this.dataTypeAdapter = dataTypeAdapter;
    return this;
  }

  @Override
  public FieldBuilder defaultValue(@NonNull Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  @Override
  @NonNull
  public IFieldInstanceAbsolute toInstance(
      @NonNull IAssemblyDefinition parent) {
    IFieldDefinition def = toDefinition();
    return toInstance(parent, def);
  }

  @Override
  @NonNull
  public IFieldInstanceAbsolute toInstance(
      @NonNull IAssemblyDefinition parent,
      @NonNull IFieldDefinition definition) {
    validate();

    IFieldInstanceAbsolute retval = mock(IFieldInstanceAbsolute.class);
    applyNamedInstance(retval, definition, parent);
    return retval;
  }

  @Override
  @NonNull
  public IFieldDefinition toDefinition() {
    return toDefinition(null);
  }

  @Override
  @NonNull
  public IFieldDefinition toDefinition(@Nullable IModule module) {
    validate();

    IFieldDefinition retval = mock(IFieldDefinition.class);
    applyDefinition(retval, module);

    Map<IEnhancedQName, IFlagInstance> flags = getFlags().stream()
        .map(builder -> builder.toInstance(retval))
        .collect(Collectors.toUnmodifiableMap(
            IFlagInstance::getQName,
            Function.identity()));

    doReturn(new ValueConstraintSet(ObjectUtils.notNull(getSource()))).when(retval).getConstraintSupport();
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
