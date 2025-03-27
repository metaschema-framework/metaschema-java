
package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A JSON schema for a given Metaschema-based model object based on a module
 * definition that can have flags, which is part of a larger JSON schema.
 * 
 * @param <D>
 *          the Java type of the Metaschema module definition
 */
public abstract class AbstractJsonSchemaModelDefinition<D extends IModelDefinition>
    extends AbstractJsonSchemaDefinition<D>
    implements IJsonSchemaModelDefinition {
  @Nullable
  private final IEnhancedQName jsonKeyFlagName;
  private final List<IJsonSchemaPropertyFlag> flagProperties;

  /**
   * Construct a new JSON schema definition based on a Metaschema module
   * model-based definition.
   * 
   * @param definition
   *          the Metaschema module definition
   * @param jsonKeyFlagName
   *          optional, the name of the definitions JSON key flag
   * @param state
   *          the generation state used to generate this JSON schema
   */
  public AbstractJsonSchemaModelDefinition(
      @NonNull D definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {
    super(definition, state);
    this.jsonKeyFlagName = jsonKeyFlagName;
    this.flagProperties = JsonSchemaHelper.buildFlagProperties(definition, jsonKeyFlagName, state);
  }

  @Override
  public String generateDefinitionName(IJsonGenerationState state) {
    return state.generateJsonSchemaDefinitionName(
        getDefinition(),
        jsonKeyFlagName == null ? null : state.toFlagName(jsonKeyFlagName),
        null);
  }

  @Override
  public IFlagInstance getJsonKeyFlag() {
    return getDefinition().getJsonKey();
  }

  @Override
  @Nullable
  public IEnhancedQName getJsonKeyFlagName() {
    return jsonKeyFlagName;
  }

  @Override
  public List<IJsonSchemaPropertyFlag> getFlagProperties() {
    return flagProperties;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    Stream<IJsonSchemaDefinable> retval = Stream.concat(
        Stream.of(this),
        getFlagProperties().stream()
            .flatMap(property -> property.collectDefinitions(visited, state)));

    IFlagInstance jsonKeyFlag = getJsonKeyFlag();
    if (jsonKeyFlag != null) {
      retval = Stream.concat(retval, Stream.of(state.getFlagDefinition(jsonKeyFlag.getDefinition())));
    }

    return retval;
  }
}
