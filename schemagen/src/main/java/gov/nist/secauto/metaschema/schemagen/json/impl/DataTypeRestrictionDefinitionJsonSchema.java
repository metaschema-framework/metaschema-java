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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DataTypeRestrictionDefinitionJsonSchema
    implements IDataTypeJsonSchema {
  @NonNull
  private final String name;
  @NonNull
  private final IValuedDefinition definition;
  @NonNull
  private final AllowedValueCollection allowedValuesCollection;

  public DataTypeRestrictionDefinitionJsonSchema(
      @NonNull IValuedDefinition definition,
      @NonNull AllowedValueCollection allowedValuesCollection,
      @NonNull IJsonGenerationState state) {
    this.name = state.getTypeNameForDefinition(definition, "Value");
    this.definition = definition;
    CollectionUtil.requireNonEmpty(allowedValuesCollection.getValues());
    this.allowedValuesCollection = allowedValuesCollection;
  }

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
    // always inline
    return true;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    // ensure the base datatype is registered
    state.getSchema(getDataTypeAdapter());

    return Stream.of(this);
  }

  @Override
  public String getDefinitionName() {
    return name;
  }

  @Override
  public void generateDefinitionJsonSchema(ObjectNode node, IJsonGenerationState state) {
    throw new UnsupportedOperationException("not used");
  }

  @Override
  public void generateInlineJsonSchema(ObjectNode node, IJsonGenerationState state) {
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
      ofArray = node.putArray("allOf");
    } else {
      // this restriction is anyOf, since any can match
      ofArray = node.putArray("anyOf");
    }

    // add the data type reference
    dataTypeSchema.generateJsonSchemaOrDefinitionRef(ObjectUtils.notNull(ofArray.addObject()), state);

    // add the enumeration
    ofArray.addObject()
        .set("enum", enumArray);
  }
}
