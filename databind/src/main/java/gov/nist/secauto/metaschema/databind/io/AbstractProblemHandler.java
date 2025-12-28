/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Abstract base class for problem handlers that can validate required fields
 * during deserialization.
 */
public abstract class AbstractProblemHandler implements IProblemHandler {
  private final boolean validateRequiredFields;

  /**
   * Construct a new problem handler with default settings.
   * <p>
   * Required field validation is enabled by default.
   */
  protected AbstractProblemHandler() {
    this(true);
  }

  /**
   * Construct a new problem handler with the specified validation setting.
   *
   * @param validateRequiredFields
   *          {@code true} to validate that required fields are present,
   *          {@code false} to skip validation
   */
  protected AbstractProblemHandler(boolean validateRequiredFields) {
    this.validateRequiredFields = validateRequiredFields;
  }

  /**
   * Determine if required field validation is enabled.
   *
   * @return {@code true} if required fields should be validated, {@code false}
   *         otherwise
   */
  protected boolean isValidateRequiredFields() {
    return validateRequiredFields;
  }

  @Override
  public void handleMissingInstances(
      IBoundDefinitionModelComplex parentDefinition,
      IBoundObject targetObject,
      Collection<? extends IBoundProperty<?>> unhandledInstances) throws IOException {
    if (isValidateRequiredFields()) {
      validateRequiredFields(parentDefinition, unhandledInstances);
    }
    applyDefaults(targetObject, unhandledInstances);
  }

  /**
   * Validate that all required fields have values or defaults.
   * <p>
   * This method handles choice groups correctly: if an instance belongs to a
   * choice and at least one sibling in that choice was provided, the instance is
   * not considered missing.
   *
   * @param parentDefinition
   *          the definition containing the unhandled instances
   * @param unhandledInstances
   *          the collection of unhandled instances to validate
   * @throws IOException
   *           if a required field is missing and has no default value
   */
  protected void validateRequiredFields(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @NonNull Collection<? extends IBoundProperty<?>> unhandledInstances) throws IOException {

    // Build a set of unhandled instance names for quick lookup
    Set<String> unhandledNames = new HashSet<>();
    for (IBoundProperty<?> instance : unhandledInstances) {
      unhandledNames.add(getInstanceName(instance));
    }

    // Build a map from instance name to its choice group (if any)
    Map<String, IChoiceInstance> instanceToChoice = buildInstanceToChoiceMap(parentDefinition);

    List<String> missingRequired = new ArrayList<>();

    for (IBoundProperty<?> instance : unhandledInstances) {
      if (isRequiredAndMissingDefault(instance)) {
        String instanceName = getInstanceName(instance);
        IChoiceInstance choice = instanceToChoice.get(instanceName);

        if (choice != null) {
          // Instance belongs to a choice group - check if any sibling was provided
          if (!isChoiceSatisfied(choice, unhandledNames)) {
            // All siblings in the choice are missing - report this as an error
            missingRequired.add(instanceName);
          }
          // else: at least one sibling was provided, choice is satisfied
        } else {
          // Not in a choice group - normal required field check
          missingRequired.add(instanceName);
        }
      }
    }

    if (!missingRequired.isEmpty()) {
      throw new IOException(String.format(
          "Missing required %s in %s: %s",
          missingRequired.size() == 1 ? "property" : "properties",
          parentDefinition.getName(),
          String.join(", ", missingRequired)));
    }
  }

  /**
   * Build a map from instance name to its containing choice instance.
   *
   * @param parentDefinition
   *          the parent definition to examine
   * @return a map of instance names to their choice groups, empty if no choices
   */
  @NonNull
  private static Map<String, IChoiceInstance> buildInstanceToChoiceMap(
      @NonNull IBoundDefinitionModelComplex parentDefinition) {
    Map<String, IChoiceInstance> result = new HashMap<>();

    if (parentDefinition instanceof IBoundDefinitionModelAssembly) {
      IBoundDefinitionModelAssembly assembly = (IBoundDefinitionModelAssembly) parentDefinition;
      for (IChoiceInstance choice : assembly.getChoiceInstances()) {
        for (INamedModelInstanceAbsolute modelInstance : choice.getNamedModelInstances()) {
          // Use the JSON name for consistency with how we track instances
          result.put(modelInstance.getJsonName(), choice);
        }
      }
    }

    return result;
  }

  /**
   * Check if a choice is satisfied.
   * <p>
   * A choice is satisfied if:
   * <ul>
   * <li>At least one alternative was provided, OR</li>
   * <li>The choice is optional (minOccurs = 0) and no alternative is required
   * </li>
   * </ul>
   *
   * @param choice
   *          the choice to check
   * @param unhandledNames
   *          the set of instance names that were NOT provided
   * @return {@code true} if the choice requirements are satisfied, {@code false}
   *         if a required alternative is missing
   */
  private static boolean isChoiceSatisfied(
      @NonNull IChoiceInstance choice,
      @NonNull Set<String> unhandledNames) {
    // Check if any alternative was provided
    for (INamedModelInstanceAbsolute modelInstance : choice.getNamedModelInstances()) {
      String name = modelInstance.getJsonName();
      if (!unhandledNames.contains(name)) {
        // This sibling was provided (not in unhandled list)
        return true;
      }
    }

    // All siblings are in the unhandled list - check if choice is optional
    // If choice.getMinOccurs() == 0, having no selection is valid
    return choice.getMinOccurs() == 0;
  }

  /**
   * Determine if the given instance is required and has no default value.
   *
   * @param instance
   *          the instance to check
   * @return {@code true} if the instance is required and has no default value
   */
  private static boolean isRequiredAndMissingDefault(@NonNull IBoundProperty<?> instance) {
    // Check if the instance has a default value
    Object defaultValue = instance.getResolvedDefaultValue();
    if (defaultValue != null) {
      // Has a default value, so it's not "missing"
      return false;
    }

    // Check if the instance is required
    if (instance instanceof IFlagInstance) {
      return ((IFlagInstance) instance).isRequired();
    } else if (instance instanceof IModelInstance) {
      return ((IModelInstance) instance).getMinOccurs() > 0;
    }

    // Unknown instance type, don't require it
    return false;
  }

  /**
   * Get a human-readable name for the instance.
   *
   * @param instance
   *          the instance to get the name for
   * @return the instance name
   */
  @NonNull
  private static String getInstanceName(@NonNull IBoundProperty<?> instance) {
    return instance.getJsonName();
  }

  /**
   * A utility method for applying default values for the provided
   * {@code unhandledInstances}.
   *
   * @param targetObject
   *          the Java object to apply default values to
   * @param unhandledInstances
   *          the collection of unhandled instances to assign default values for
   * @throws IOException
   *           if an error occurred while determining the default value for an
   *           instance
   */
  protected static void applyDefaults(
      @NonNull Object targetObject,
      @NonNull Collection<? extends IBoundProperty<?>> unhandledInstances) throws IOException {
    for (IBoundProperty<?> instance : unhandledInstances) {
      assert instance != null;
      Object value = instance.getResolvedDefaultValue();
      if (value != null) {
        instance.setValue(targetObject, value);
      }
    }
  }
}
