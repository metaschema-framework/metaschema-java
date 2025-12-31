/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
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
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
    // Delegate to the context-aware version with null context
    handleMissingInstances(parentDefinition, targetObject, unhandledInstances, null);
  }

  @Override
  public void handleMissingInstances(
      IBoundDefinitionModelComplex parentDefinition,
      IBoundObject targetObject,
      Collection<? extends IBoundProperty<?>> unhandledInstances,
      @Nullable ValidationContext context) throws IOException {
    if (isValidateRequiredFields()) {
      validateRequiredFields(parentDefinition, unhandledInstances, context);
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
   * @param context
   *          the validation context with location and path information, may be
   *          null
   * @throws IOException
   *           if a required field is missing and has no default value
   */
  protected void validateRequiredFields(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @NonNull Collection<? extends IBoundProperty<?>> unhandledInstances,
      @Nullable ValidationContext context) throws IOException {

    // Build a set of unhandled instance names for quick lookup
    Set<String> unhandledNames = new HashSet<>();
    for (IBoundProperty<?> instance : unhandledInstances) {
      assert instance != null;
      unhandledNames.add(getInstanceName(instance, context));
    }

    // Build a map from instance name to its choice group (if any)
    Map<String, IChoiceInstance> instanceToChoice = buildInstanceToChoiceMap(parentDefinition, context);

    // Collect missing required properties grouped by type
    List<IBoundProperty<?>> missingFlags = new ArrayList<>();
    List<IBoundProperty<?>> missingFields = new ArrayList<>();
    List<IBoundProperty<?>> missingAssemblies = new ArrayList<>();

    for (IBoundProperty<?> instance : unhandledInstances) {
      assert instance != null;
      if (isRequiredAndMissingDefault(instance)) {
        String instanceName = getInstanceName(instance, context);
        IChoiceInstance choice = instanceToChoice.get(instanceName);

        if (choice != null) {
          // Instance belongs to a choice group - check if any sibling was provided
          if (!isChoiceSatisfied(choice, unhandledNames, context)) {
            // All siblings in the choice are missing - report this as an error
            addToTypeList(instance, missingFlags, missingFields, missingAssemblies);
          }
          // else: at least one sibling was provided, choice is satisfied
        } else {
          // Not in a choice group - normal required field check
          addToTypeList(instance, missingFlags, missingFields, missingAssemblies);
        }
      }
    }

    if (!missingFlags.isEmpty() || !missingFields.isEmpty() || !missingAssemblies.isEmpty()) {
      throw new IOException(formatMissingPropertiesMessage(
          parentDefinition, missingFlags, missingFields, missingAssemblies, context));
    }
  }

  /**
   * Add an instance to the appropriate type-specific list.
   *
   * @param instance
   *          the instance to categorize
   * @param missingFlags
   *          list for flag instances
   * @param missingFields
   *          list for field instances
   * @param missingAssemblies
   *          list for assembly instances
   */
  private static void addToTypeList(
      @NonNull IBoundProperty<?> instance,
      @NonNull List<IBoundProperty<?>> missingFlags,
      @NonNull List<IBoundProperty<?>> missingFields,
      @NonNull List<IBoundProperty<?>> missingAssemblies) {
    if (instance instanceof IFlagInstance) {
      missingFlags.add(instance);
    } else if (instance instanceof IFieldInstance) {
      missingFields.add(instance);
    } else if (instance instanceof IAssemblyInstance) {
      missingAssemblies.add(instance);
    } else {
      // Default to fields for unknown types
      missingFields.add(instance);
    }
  }

  /**
   * Format a comprehensive error message for missing required properties.
   *
   * @param parentDefinition
   *          the parent definition containing the properties
   * @param missingFlags
   *          missing flag instances
   * @param missingFields
   *          missing field instances
   * @param missingAssemblies
   *          missing assembly instances
   * @param context
   *          the validation context, may be null
   * @return a formatted error message
   */
  @NonNull
  private static String formatMissingPropertiesMessage(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @NonNull List<IBoundProperty<?>> missingFlags,
      @NonNull List<IBoundProperty<?>> missingFields,
      @NonNull List<IBoundProperty<?>> missingAssemblies,
      @Nullable ValidationContext context) {

    StringBuilder message = new StringBuilder();
    String parentName = getParentName(parentDefinition, context);
    Format format = context != null ? context.getFormat() : Format.JSON;

    int totalMissing = missingFlags.size() + missingFields.size() + missingAssemblies.size();

    if (totalMissing == 1) {
      // Single missing property - use specific format
      IBoundProperty<?> missing = ObjectUtils.notNull(!missingFlags.isEmpty() ? missingFlags.get(0)
          : !missingFields.isEmpty() ? missingFields.get(0)
              : missingAssemblies.get(0));
      String type = getPropertyTypeName(missing, format, false);
      String name = getInstanceName(missing, context);
      message.append(String.format("Missing required %s '%s' in '%s'", type, name, parentName));
    } else if (hasSingleType(missingFlags, missingFields, missingAssemblies)) {
      // Multiple properties of single type
      List<IBoundProperty<?>> list = !missingFlags.isEmpty() ? missingFlags
          : !missingFields.isEmpty() ? missingFields
              : missingAssemblies;
      String type = getPropertyTypeName(ObjectUtils.notNull(list.get(0)), format, true);
      String names = formatNameList(list, context);
      message.append(String.format("Missing required %s in '%s': %s", type, parentName, names));
    } else {
      // Multiple properties of different types
      message.append(String.format("Missing required properties in '%s':", parentName));
      if (!missingFlags.isEmpty()) {
        message.append("\n  ").append(getFormatPropertyGroupLabel(true, format))
            .append(": ").append(formatNameList(missingFlags, context));
      }
      if (!missingFields.isEmpty()) {
        message.append("\n  ").append(getFormatPropertyGroupLabel(false, format))
            .append(": ").append(formatNameList(missingFields, context));
      }
      if (!missingAssemblies.isEmpty()) {
        message.append("\n  ").append(getFormatPropertyGroupLabel(false, format))
            .append(": ").append(formatNameList(missingAssemblies, context));
      }
    }

    // Add location and path context
    if (context != null) {
      String location = context.formatLocation();
      if (!location.isEmpty()) {
        message.append("\n  Location: ").append(location);
      }
      String path = context.getPath();
      if (!"/".equals(path) && !path.isEmpty()) {
        message.append("\n  Path: ").append(path);
      }
    }

    return ObjectUtils.notNull(message.toString());
  }

  /**
   * Get the user-friendly name for a property type in the given format.
   * <p>
   * This method maps Metaschema concepts to format-appropriate terminology:
   * <ul>
   * <li>XML: flags are "attribute", fields/assemblies are "element"</li>
   * <li>JSON: all properties are "property"</li>
   * <li>YAML: all properties are "property"</li>
   * </ul>
   *
   * @param isFlag
   *          {@code true} if the property is a flag instance
   * @param format
   *          the format being parsed
   * @param plural
   *          {@code true} for plural form, {@code false} for singular
   * @return the user-friendly type name
   */
  @NonNull
  private static String getFormatPropertyTypeName(boolean isFlag, @NonNull Format format, boolean plural) {
    switch (format) {
    case XML:
      if (isFlag) {
        return plural ? "attributes" : "attribute";
      }
      return plural ? "elements" : "element";
    case JSON:
    case YAML:
      return plural ? "properties" : "property";
    default:
      // Fallback for any future formats - use generic "property"
      return plural ? "properties" : "property";
    }
  }

  /**
   * Get the user-friendly label for a group of properties in the given format.
   * <p>
   * Used when listing multiple properties of the same type in error messages.
   * Delegates to {@link #getFormatPropertyTypeName(boolean, Format, boolean)} and
   * capitalizes the result.
   *
   * @param isFlags
   *          {@code true} if listing flag instances
   * @param format
   *          the format being parsed
   * @return the plural label (e.g., "Attributes", "Elements", "Properties")
   */
  @NonNull
  private static String getFormatPropertyGroupLabel(boolean isFlags, @NonNull Format format) {
    String typeName = getFormatPropertyTypeName(isFlags, format, true);
    return Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1);
  }

  /**
   * Check if only one type list has entries.
   */
  private static boolean hasSingleType(
      List<IBoundProperty<?>> flags,
      List<IBoundProperty<?>> fields,
      List<IBoundProperty<?>> assemblies) {
    int nonEmpty = 0;
    if (!flags.isEmpty()) {
      nonEmpty++;
    }
    if (!fields.isEmpty()) {
      nonEmpty++;
    }
    if (!assemblies.isEmpty()) {
      nonEmpty++;
    }
    return nonEmpty == 1;
  }

  /**
   * Get the property type name for error messages in format-appropriate terms.
   * <p>
   * Delegates to {@link #getFormatPropertyTypeName(boolean, Format, boolean)}
   * based on whether the instance is a flag.
   *
   * @param instance
   *          the property instance
   * @param format
   *          the format being parsed
   * @param plural
   *          {@code true} for plural form, {@code false} for singular
   * @return the user-friendly type name appropriate for the format
   */
  @NonNull
  private static String getPropertyTypeName(
      @NonNull IBoundProperty<?> instance,
      @NonNull Format format,
      boolean plural) {
    boolean isFlag = instance instanceof IFlagInstance;
    return getFormatPropertyTypeName(isFlag, format, plural);
  }

  /**
   * Format a list of property names as a comma-separated string.
   */
  @NonNull
  private static String formatNameList(
      @NonNull List<IBoundProperty<?>> instances,
      @Nullable ValidationContext context) {
    return ObjectUtils.notNull(instances.stream()
        .map(i -> getInstanceName(ObjectUtils.notNull(i), context))
        .collect(Collectors.joining(", ")));
  }

  /**
   * Get the parent definition name for error messages.
   *
   * @param parentDefinition
   *          the parent definition
   * @param context
   *          the validation context, may be null
   * @return the effective name of the parent
   */
  @NonNull
  private static String getParentName(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @Nullable ValidationContext context) {
    // Use effective name which is format-appropriate
    return parentDefinition.getEffectiveName();
  }

  /**
   * Build a map from instance name to its containing choice instance.
   *
   * @param parentDefinition
   *          the parent definition to examine
   * @param context
   *          the validation context, may be null
   * @return a map of instance names to their choice groups, empty if no choices
   */
  @NonNull
  private static Map<String, IChoiceInstance> buildInstanceToChoiceMap(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @Nullable ValidationContext context) {
    Map<String, IChoiceInstance> result = new HashMap<>();

    if (parentDefinition instanceof IBoundDefinitionModelAssembly) {
      IBoundDefinitionModelAssembly assembly = (IBoundDefinitionModelAssembly) parentDefinition;
      for (IChoiceInstance choice : assembly.getChoiceInstances()) {
        for (INamedModelInstanceAbsolute modelInstance : choice.getNamedModelInstances()) {
          // Use effective name for format-appropriate matching
          result.put(modelInstance.getEffectiveName(), choice);
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
   * @param context
   *          the validation context, may be null
   * @return {@code true} if the choice requirements are satisfied, {@code false}
   *         if a required alternative is missing
   */
  private static boolean isChoiceSatisfied(
      @NonNull IChoiceInstance choice,
      @NonNull Set<String> unhandledNames,
      @Nullable ValidationContext context) {
    // Check if any alternative was provided
    for (INamedModelInstanceAbsolute modelInstance : choice.getNamedModelInstances()) {
      // Use effective name for format-appropriate matching
      String name = modelInstance.getEffectiveName();
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
   * <p>
   * Uses {@code getEffectiveName()} when available to return the
   * format-appropriate name (XML element/attribute name for XML, JSON property
   * name for JSON). Falls back to {@code getJsonName()} if effective name is not
   * available.
   *
   * @param instance
   *          the instance to get the name for
   * @param context
   *          the validation context, may be null
   * @return the instance name
   */
  @NonNull
  private static String getInstanceName(
      @NonNull IBoundProperty<?> instance,
      @Nullable ValidationContext context) {
    // Check for specific instance types that have getEffectiveName()
    if (instance instanceof IFlagInstance) {
      return ((IFlagInstance) instance).getEffectiveName();
    } else if (instance instanceof IFieldInstance) {
      return ((IFieldInstance) instance).getEffectiveName();
    } else if (instance instanceof IAssemblyInstance) {
      return ((IAssemblyInstance) instance).getEffectiveName();
    }
    // Fall back to JSON name for other types
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
