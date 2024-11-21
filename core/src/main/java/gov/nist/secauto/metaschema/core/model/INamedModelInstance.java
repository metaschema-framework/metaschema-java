/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface INamedModelInstance extends IModelInstance, INamedInstance {

  /**
   * Tests if the provided instance represents complex data. The data is complex
   * if one of the following is true:
   * <ul>
   * <li>The instance is a {@link IAssemblyInstance}.</li>
   * <li>The instance is a {@link IFieldInstance} that has flags.</li>
   * </ul>
   *
   * This method can be used as a {@link Predicate}.
   *
   * @param instance
   *          the instance to test
   * @return {@code true} if the data is complex, or {@code false} otherwise
   */
  static boolean complexObjectFilter(INamedModelInstance instance) {
    boolean retval = true;
    if (instance instanceof IFieldInstance) {
      IFieldInstance field = (IFieldInstance) instance;
      retval = !field.getDefinition().getFlagInstances().isEmpty();
    }
    return retval;
  }

  @Override
  @NonNull
  IModelDefinition getDefinition();

  /**
   * Indicates if a flag's value can be used as a property name in the containing
   * object in JSON who's value will be the object containing the flag. In such
   * cases, the flag will not appear in the object. This is only allowed if the
   * flag is required, as determined by a {@code true} result from
   * {@link IFlagInstance#isRequired()}. The {@link IFlagInstance} can be
   * retrieved using {@link #getEffectiveJsonKey()}.
   *
   * @return {@code true} if the flag's value can be used as a property name, or
   *         {@code false} otherwise
   * @see #getEffectiveJsonKey()
   */
  // TODO: remove once moved to the instance side
  default boolean hasJsonKey() {
    return getEffectiveJsonKey() != null;
  }

  /**
   * Get the JSON key flag instance for this model instance, if one is configured.
   *
   * @return the JSON key flag instance or {@code null} if a JSON key is
   *         configured
   */
  @Nullable
  IFlagInstance getEffectiveJsonKey();

  /**
   * Get the JSON key associated with this instance.
   *
   * @return the configured JSON key or {@code null} if no JSON key is configured
   */
  @Nullable
  IFlagInstance getJsonKey();
}
