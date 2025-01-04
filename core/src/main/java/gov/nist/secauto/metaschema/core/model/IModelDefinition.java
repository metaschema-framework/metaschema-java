/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;
import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metaschema definition for a complex object that may contain flags.
 */
public interface IModelDefinition extends IDefinition, IContainer {
  /**
   * Tests if the provided definition represents complex data. The data is complex if one of the
   * following is true:
   * <ul>
   * <li>The instance is a {@link IAssemblyDefinition}.
   * <li>The instance is a {@link IFieldDefinition} that has flags.
   * </ul>
   *
   * This method can be used as a {@link Predicate}.
   *
   * @param definition
   *          the definition to test
   * @return {@code true} if the data is complex, or {@code false} otherwise
   */
  static boolean complexObjectFilter(IModelDefinition definition) {
    boolean retval = true;
    if (definition instanceof IFieldDefinition) {
      IFieldDefinition field = (IFieldDefinition) definition;
      retval = !field.getFlagInstances().isEmpty();
    }
    return retval;
  }

  @Override
  default boolean hasChildren() {
    return !getFlagInstances().isEmpty();
  }

  /**
   * Retrieves a flag instance, by the flag's effective name-based qualified name index.
   *
   * @param index
   *          the flag's name-based qualified name index
   * @return the matching flag instance, or {@code null} if there is no flag matching the specified
   *         name
   */
  @Nullable
  IFlagInstance getFlagInstanceByName(@NonNull Integer index);

  /**
   * Retrieves the flag instances for all flags defined on the containing definition.
   *
   * @return the flags
   */
  @NonNull
  Collection<? extends IFlagInstance> getFlagInstances();

  /**
   * Retrieves the flag instance to use as as the property name for the containing object in JSON
   * who's value will be the object containing the flag.
   *
   * @return the flag instance if a JSON key is configured, or {@code null} otherwise
   */
  // TODO: remove once moved to the instance side
  @Nullable
  IFlagInstance getJsonKey();
}
