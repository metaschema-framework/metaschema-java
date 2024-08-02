/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractGenerationState.AllowedValueCollection;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DataTypeRestrictionDefinitionJsonSchema
    extends AbstractDefineableJsonSchema
    implements IDataTypeJsonSchema, IDefinitionJsonSchema<IValuedDefinition> {
  @NonNull
  private final IValuedDefinition definition;
  @NonNull
  private final AllowedValueCollection allowedValuesCollection;

  public DataTypeRestrictionDefinitionJsonSchema(
      @NonNull IValuedDefinition definition,
      @NonNull AllowedValueCollection allowedValuesCollection) {
    this.definition = definition;
    CollectionUtil.requireNonEmpty(allowedValuesCollection.getValues());
    this.allowedValuesCollection = allowedValuesCollection;
  }

  @Override
  public IKey getKey() {
    return IKey.of(definition, null, null, null);
  }

  @Override
  @NonNull
  public IValuedDefinition getDefinition() {
    return definition;
  }

  @Override
  public IDataTypeAdapter<?> getDataTypeAdapter() {
    return getDefinition().getJavaTypeAdapter();
  }

  @NonNull
  protected AllowedValueCollection getAllowedValuesCollection() {
    return allowedValuesCollection;
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    // // inline if the definition is inline
    // return state.isInline(definition);
    // always inline
    return true;
  }

  @Override
  protected String generateDefinitionName(IJsonGenerationState state) {
    return state.getTypeNameForDefinition(definition, "Value");
  }

  @Override
  public void generateInlineSchema(ObjectNode obj, IJsonGenerationState state) {
    // generate a restriction on the built-in type for the enumerated values
    ArrayNode enumArray = JsonNodeFactory.instance.arrayNode();

    AllowedValueCollection allowedValuesCollection = getAllowedValuesCollection();
    for (IAllowedValue allowedValue : allowedValuesCollection.getValues()) {
      switch (getDefinition().getJavaTypeAdapter().getJsonRawType()) {
      case STRING:
        enumArray.add(allowedValue.getValue());
        break;
      case BOOLEAN:
        enumArray.add(Boolean.parseBoolean(allowedValue.getValue()));
        break;
      case INTEGER:
        enumArray.add(new BigInteger(allowedValue.getValue())); // NOPMD unavoidable
        break;
      case NUMBER:
        enumArray.add(new BigDecimal(allowedValue.getValue(), MathContext.DECIMAL64)); // NOPMD unavoidable
        break;
      default:
        throw new UnsupportedOperationException(getDefinition().getJavaTypeAdapter().getJsonRawType().toString());
      }
    }
    // get schema for the built-in type
    IDataTypeJsonSchema dataTypeSchema = state.getSchema(getDefinition().getJavaTypeAdapter());

    // if other values are allowed, we need to make a union of the restriction type
    // and the base
    // built-in type
    ArrayNode ofArray;
    if (allowedValuesCollection.isClosed()) {
      // this restriction is allOf, since both must match
      ofArray = obj.putArray("allOf");
    } else {
      // this restriction is anyOf, since any can match
      ofArray = obj.putArray("anyOf");
    }

    // add the data type reference
    dataTypeSchema.generateSchemaOrRef(ObjectUtils.notNull(ofArray.addObject()), state);

    // add the enumeration
    ofArray.addObject()
        .set("enum", enumArray);
  }

  @Override
  public void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state) {
    // ensure the base datatype is registered
    state.getSchema(getDataTypeAdapter());

    // Generate a definition if the restricted definition is not inline
    IKey key = getKey();
    gatheredDefinitions.put(key, state.getSchema(getKey()));
  }
}
