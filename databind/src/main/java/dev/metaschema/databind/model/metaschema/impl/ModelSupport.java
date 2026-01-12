/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import java.math.BigInteger;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.StaticMetapathException;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.IDocumentNodeItem;
import dev.metaschema.core.metapath.type.DataTypeItemType;
import dev.metaschema.core.metapath.type.IAtomicOrUnionType;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.IGroupable;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.annotations.ModelUtil;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.GroupingAs;
import dev.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineAssembly.RootName;
import dev.metaschema.databind.model.metaschema.binding.Property;
import dev.metaschema.databind.model.metaschema.binding.Remarks;
import dev.metaschema.databind.model.metaschema.binding.UseName;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Utility class providing support methods for model construction.
 * <p>
 * This class provides common utility methods used when building model
 * structures from Metaschema bindings.
 */
public final class ModelSupport {
  private ModelSupport() {
    // disable construction
  }

  /**
   * Parses a list of property bindings into a map of attributable keys to values.
   *
   * @param props
   *          the list of property bindings to parse
   * @return an unmodifiable map of property keys to their values
   */
  @NonNull
  public static Map<IAttributable.Key, Set<String>> parseProperties(@NonNull List<Property> props) {
    return CollectionUtil.unmodifiableMap(ObjectUtils.notNull(props.stream()
        .collect(
            Collectors.groupingBy(
                prop -> {
                  String name = ObjectUtils.requireNonNull(prop.getName());
                  URI namespace = prop.getNamespace();
                  return namespace == null ? IAttributable.key(name)
                      : IAttributable.key(name, ObjectUtils.notNull(namespace.toASCIIString()));
                },
                Collectors.mapping(
                    prop -> ObjectUtils.requireNonNull(prop.getValue()),
                    Collectors.toCollection(LinkedHashSet::new))))));
  }

  /**
   * Converts a yes/no text value to a boolean.
   *
   * @param allowOther
   *          the text value to convert
   * @return {@code true} if the value equals "yes", {@code false} otherwise
   */
  public static boolean yesOrNo(String allowOther) {
    return "yes".equals(allowOther);
  }

  /**
   * Translate a text scope value to the equivalent enumerated value.
   *
   * @param value
   *          the text scope value
   * @return the enumerated value
   */
  @NonNull
  public static IDefinition.ModuleScope moduleScope(@NonNull String value) {
    IDefinition.ModuleScope retval;
    switch (value) {
    case "local":
      retval = IDefinition.ModuleScope.PRIVATE;
      break;
    case "global":
    default:
      retval = IDefinition.ModuleScope.PUBLIC;
    }
    return retval;
  }

  /**
   * Converts a BigInteger index to an Integer.
   *
   * @param index
   *          the BigInteger index, or {@code null}
   * @return the Integer value, or {@code null} if the input is {@code null}
   */
  @Nullable
  public static Integer index(@Nullable BigInteger index) {
    return index == null ? null : index.intValueExact();
  }

  /**
   * Extracts the use name from a UseName binding.
   *
   * @param useName
   *          the UseName binding, or {@code null}
   * @return the use name string, or {@code null} if not present
   */
  @Nullable
  public static String useName(@Nullable UseName useName) {
    return useName == null ? null : useName.getName();
  }

  /**
   * Extracts the use index from a UseName binding.
   *
   * @param useName
   *          the UseName binding, or {@code null}
   * @return the use index as an Integer, or {@code null} if not present
   */
  @Nullable
  public static Integer useIndex(@Nullable UseName useName) {
    Integer retval = null;
    if (useName != null) {
      BigInteger index = useName.getIndex();
      if (index != null) {
        retval = index.intValueExact();
      }
    }
    return retval;
  }

  /**
   * Extracts the remarks markup from a Remarks binding.
   *
   * @param remarks
   *          the Remarks binding, or {@code null}
   * @return the remarks as MarkupMultiline, or {@code null} if not present
   */
  @Nullable
  public static MarkupMultiline remarks(@Nullable Remarks remarks) {
    return remarks == null ? null : remarks.getRemark();
  }

  /**
   * Resolves a data type name to its corresponding data type adapter.
   *
   * @param dataType
   *          the data type name string, or {@code null} for the default type
   * @param source
   *          the source context for type resolution
   * @return the data type adapter for the specified type
   * @throws IllegalStateException
   *           if the data type is unrecognized or has no adapter
   */
  @NonNull
  public static IDataTypeAdapter<?> dataType(
      @Nullable String dataType,
      @NonNull ISource source) {
    IDataTypeAdapter<?> retval;
    if (dataType == null) {
      retval = MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE;
    } else {
      IEnhancedQName qname = IEnhancedQName.of(MetapathConstants.NS_METAPATH, dataType);
      IAtomicOrUnionType<?> type;
      try {
        source.getStaticContext();
        type = StaticContext.lookupAtomicType(qname);
      } catch (StaticMetapathException ex) {
        throw new IllegalStateException("Unrecognized data type: " + qname, ex);

      }
      if (!(type instanceof DataTypeItemType)) {
        throw new IllegalStateException("No type adapter registered for data type: " + qname);
      }
      retval = ((DataTypeItemType<?>) type).getAdapter();
    }
    return retval;
  }

  /**
   * Resolves a default value string using the specified data type adapter.
   *
   * @param defaultValue
   *          the default value string, or {@code null}
   * @param javaTypeAdapter
   *          the data type adapter to use for parsing
   * @return the parsed default value object, or {@code null} if no default
   */
  @Nullable
  public static Object defaultValue(
      @Nullable String defaultValue,
      @NonNull IDataTypeAdapter<?> javaTypeAdapter) {
    return defaultValue == null ? null : ModelUtil.resolveDefaultValue(defaultValue, javaTypeAdapter);
  }

  /**
   * Parses a max-occurs value to an integer.
   *
   * @param maxOccurs
   *          the max-occurs string value
   * @return -1 for "unbounded", otherwise the parsed integer value
   */
  public static int maxOccurs(@NonNull String maxOccurs) {
    return "unbounded".equals(maxOccurs) ? -1 : Integer.parseInt(maxOccurs);
  }

  /**
   * Extracts the root name from a RootName binding.
   *
   * @param rootName
   *          the RootName binding, or {@code null}
   * @return the root name string, or {@code null} if not present
   */
  public static String rootName(@Nullable RootName rootName) {
    return rootName == null ? null : rootName.getName();
  }

  /**
   * Extracts the root index from a RootName binding.
   *
   * @param rootName
   *          the RootName binding, or {@code null}
   * @return the root index as an Integer, or {@code null} if not present
   */
  public static Integer rootIndex(@Nullable RootName rootName) {
    Integer retval = null;
    if (rootName != null) {
      BigInteger index = rootName.getIndex();
      if (index != null) {
        retval = index.intValueExact();
      }
    }
    return retval;
  }

  /**
   * Determines if a field should be wrapped in XML based on the in-xml value.
   *
   * @param inXml
   *          the in-xml string value, or {@code null}
   * @return {@code true} if the field should be wrapped, {@code false} otherwise
   */
  public static boolean fieldInXml(@Nullable String inXml) {
    boolean retval = IFieldInstance.DEFAULT_FIELD_IN_XML_WRAPPED;
    if (inXml != null) {
      switch (inXml) {
      case "WRAPPED":
      case "WITH_WRAPPER":
        retval = true;
        break;
      default:
        retval = false;
        break;
      }
    }
    return retval;
  }

  /**
   * Converts a {@link GroupingAs} binding to an {@link IGroupAs} instance.
   *
   * @param groupAs
   *          the grouping-as binding, or {@code null} for singleton grouping
   * @param module
   *          the containing module
   * @return the group-as instance
   */
  @NonNull
  public static IGroupAs groupAs(
      @Nullable GroupingAs groupAs,
      @NonNull IModule module) {
    return groupAs == null
        ? IGroupAs.SINGLETON_GROUP_AS
        : new GroupAsImpl(groupAs, module);
  }

  /**
   * Resolves the JSON group-as behavior from a string representation.
   *
   * @param inJson
   *          the JSON group-as behavior string, or {@code null} for default
   * @return the resolved JSON group-as behavior
   */
  @NonNull
  public static JsonGroupAsBehavior groupAsJsonBehavior(@Nullable String inJson) {
    JsonGroupAsBehavior retval = IGroupable.DEFAULT_JSON_GROUP_AS_BEHAVIOR;
    if (inJson != null) {
      switch (inJson) {
      case "ARRAY":
        retval = JsonGroupAsBehavior.LIST;
        break;
      case "SINGLETON_OR_ARRAY":
        retval = JsonGroupAsBehavior.SINGLETON_OR_LIST;
        break;
      case "BY_KEY":
        retval = JsonGroupAsBehavior.KEYED;
        break;
      default:
        retval = IGroupable.DEFAULT_JSON_GROUP_AS_BEHAVIOR;
        break;
      }
    }
    return retval;
  }

  /**
   * Resolves the XML group-as behavior from a string representation.
   *
   * @param inXml
   *          the XML group-as behavior string, or {@code null} for default
   * @return the resolved XML group-as behavior
   */
  @NonNull
  public static XmlGroupAsBehavior groupAsXmlBehavior(@Nullable String inXml) {
    XmlGroupAsBehavior retval = IGroupable.DEFAULT_XML_GROUP_AS_BEHAVIOR;
    if (inXml != null) {
      switch (inXml) {
      case "GROUPED":
        retval = XmlGroupAsBehavior.GROUPED;
        break;
      case "UNGROUPED":
        retval = XmlGroupAsBehavior.UNGROUPED;
        break;
      default:
        retval = IGroupable.DEFAULT_XML_GROUP_AS_BEHAVIOR;
        break;
      }
    }
    return retval;
  }

  /**
   * Retrieves a node item from a binding module by qualified name and position.
   *
   * @param <NODE>
   *          the node item type
   * @param module
   *          the binding module to retrieve from
   * @param definitionQName
   *          the qualified name of the definition
   * @param position
   *          the position index of the node item
   * @return the node item at the specified position, or {@code null} if not found
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public static <NODE extends IAssemblyNodeItem> NODE toNodeItem(
      @NonNull IBindingMetaschemaModule module,
      @NonNull IEnhancedQName definitionQName,
      int position) {
    IDocumentNodeItem moduleNodeItem = module.getSourceNodeItem();
    return (NODE) moduleNodeItem.getModelItemsByName(definitionQName).get(position);
  }
}
