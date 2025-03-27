
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaPropertyNamed extends IJsonSchemaProperty {
  /**
   * Get the name of the JSON property.
   * 
   * @return the JSON property name
   */
  @NonNull
  String getName();

  /**
   * Determine if the property is required or not.
   * 
   * @return {@code true} if the property is required or {@code false} otherwise
   */
  boolean isRequired();
}
