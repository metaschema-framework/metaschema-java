/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import java.lang.reflect.Field;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.impl.DefinitionField;
import dev.metaschema.databind.model.impl.InstanceModelFieldComplex;
import dev.metaschema.databind.model.impl.InstanceModelFieldScalar;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a field instance bound to a Java field.
 * <p>
 * This interface handles both scalar (simple type) and complex (class-bound)
 * field instances.
 *
 * @param <ITEM>
 *          the Java type for associated bound objects
 */
public interface IBoundInstanceModelField<ITEM> extends IBoundInstanceModelNamed<ITEM>, IFieldInstanceAbsolute {

  @Override
  IBoundDefinitionModelField<ITEM> getDefinition();

  /**
   * Create a new bound field instance.
   *
   * @param field
   *          the Java field the instance is bound to
   * @param containingDefinition
   *          the definition containing the instance
   * @return the new instance
   */
  @NonNull
  static IBoundInstanceModelField<?> newInstance(
      @NonNull Field field,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    Class<?> itemType = IBoundInstanceModel.getItemType(field);

    IBoundInstanceModelField<?> retval;
    if (IBoundObject.class.isAssignableFrom(itemType)) {
      IBindingContext bindingContext = containingDefinition.getBindingContext();
      IBoundDefinitionModel<?> definition = bindingContext.getBoundDefinitionForClass(
          ObjectUtils.notNull(itemType.asSubclass(IBoundObject.class)));
      if (definition == null) {
        throw new IllegalStateException(String.format(
            "The field '%s' on class '%s' is not bound to a Metaschema field",
            field.toString(),
            field.getDeclaringClass().getName()));
      }
      retval = InstanceModelFieldComplex.newInstance(field, (DefinitionField) definition, containingDefinition);
    } else {

      retval = InstanceModelFieldScalar.newInstance(field, containingDefinition);
    }
    return retval;
  }

  @Override
  default boolean canHandleXmlQName(IEnhancedQName qname) {
    boolean retval;
    if (isEffectiveValueWrappedInXml()) {
      retval = qname.equals(getQName());
    } else {
      IDataTypeAdapter<?> adapter = getDefinition().getJavaTypeAdapter();
      // we are to parse the data type
      retval = adapter.canHandleQName(qname);
    }
    return retval;
  }
}
