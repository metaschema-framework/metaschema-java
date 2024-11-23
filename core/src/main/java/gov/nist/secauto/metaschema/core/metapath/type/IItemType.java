/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyRawItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.ArrayTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.KindAssemblyTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.KindDocumentTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.KindFieldTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.KindFlagTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.MapTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.NodeItemTest;
import gov.nist.secauto.metaschema.core.metapath.type.impl.TypeConstants;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IItemType {
  @NonNull
  static IItemType item() {
    return AnyItemType.instance();
  }

  static IItemType function() {
    return AnyRawItemType.ANY_FUNCTION;
  }

  // static IFunctionTest function(@NonNull ISequenceType result, @NonNull
  // ISequenceType... args) {
  //
  // }

  @NonNull
  static IItemType map() {
    return AnyRawItemType.ANY_MAP;
  }

  @NonNull
  static IMapTest map(@NonNull IAtomicOrUnionType key, @NonNull ISequenceType value) {
    return new MapTestImpl(key, value);
  }

  @NonNull
  static IItemType array() {
    return AnyRawItemType.ANY_ARRAY;
  }

  @NonNull
  static IItemType array(@NonNull ISequenceType value) {
    return new ArrayTestImpl(value);
  }

  @NonNull
  static IAtomicOrUnionType anyAtomic() {
    return TypeConstants.ANY_ATOMIC_TYPE;
  }

  @NonNull
  static IKindTest<INodeItem> node() {
    return NodeItemTest.ANY_NODE;
  }

  @NonNull
  static IKindTest<IModuleNodeItem> module() {
    return NodeItemTest.ANY_MODULE;
  }

  @NonNull
  static IKindTest<IDocumentNodeItem> document() {
    return NodeItemTest.ANY_DOCUMENT;
  }

  @NonNull
  static IKindTest<IDocumentNodeItem> document(@NonNull IKindTest<IAssemblyNodeItem> test) {
    return new KindDocumentTestImpl(test);
  }

  /**
   * Matches any assembly regardless of its name or type.
   *
   * @return the test
   */
  @NonNull
  static IKindTest<IAssemblyNodeItem> assembly() {
    return NodeItemTest.ANY_ASSEMBLY;
  }

  /**
   * Matches an assembly with the provided name and a type matching the provided
   * name of a specific assembly definition.
   * <p>
   * If used as part of a {@link IKindDocumentTest}, the the provided
   * {@code instanceName} will match the root assembly name of the document's
   * root.
   *
   * @param instanceName
   *          the name of the assembly root definition or instance to match
   *          depending on the use context
   * @param typeName
   *          the name of the assembly definition to match
   * @param staticContext
   *          the static context in which the test was declared
   * @return the test
   */
  @NonNull
  static IKindTest<IAssemblyNodeItem> assembly(
      @NonNull IEnhancedQName instanceName,
      @Nullable String typeName,
      @NonNull StaticContext staticContext) {
    return new KindAssemblyTestImpl(instanceName, typeName, staticContext);
  }

  /**
   * Matches an assembly with any name and a type matching the provided name of a
   * specific assembly definition.
   *
   * @param typeName
   *          the name of the assembly definition to match
   * @param staticContext
   *          the static context in which the test was declared
   * @return the test
   */
  @NonNull
  static IKindTest<IAssemblyNodeItem> assembly(
      @NonNull String typeName,
      @NonNull StaticContext staticContext) {
    return new KindAssemblyTestImpl(null, typeName, staticContext);
  }

  /**
   * Matches any field regardless of its name or type.
   *
   * @return the test
   */
  @NonNull
  static IKindTest<IFieldNodeItem> field() {
    return NodeItemTest.ANY_FIELD;
  }

  /**
   * Matches an field with the provided name and a type matching the provided name
   * of a specific field definition.
   *
   * @param instanceName
   *          the name of the field instance to match depending on the use context
   * @param typeName
   *          the name of the field definition to match
   * @param staticContext
   *          the static context in which the test was declared
   * @return the test
   */
  @NonNull
  static IKindTest<IFieldNodeItem> field(
      @NonNull IEnhancedQName instanceName,
      @Nullable String typeName,
      @NonNull StaticContext staticContext) {
    return new KindFieldTestImpl(instanceName, typeName, staticContext);
  }

  /**
   * Matches an field with any name and a type matching the provided name of a
   * specific field definition.
   *
   * @param typeName
   *          the name of the field definition to match
   * @param staticContext
   *          the static context in which the test was declared
   * @return the test
   */
  @NonNull
  static IKindTest<IFieldNodeItem> field(@NonNull String typeName, @NonNull StaticContext staticContext) {
    return new KindFieldTestImpl(null, typeName, staticContext);
  }

  /**
   * Matches any flag regardless of its name or type.
   *
   * @return the test
   */
  @NonNull
  static IKindTest<IFlagNodeItem> flag() {
    return NodeItemTest.ANY_FLAG;
  }

  /**
   * Matches an flag with the provided name and a type matching the provided name
   * of a specific globally-scoped flag definition.
   *
   * @param instanceName
   *          the name of the flag instance to match
   * @param typeName
   *          the name of the flag definition or value type to match
   * @param staticContext
   *          the static context in which the test was declared
   * @return the test
   */
  @NonNull
  static IKindTest<IFlagNodeItem> flag(
      @NonNull IEnhancedQName instanceName,
      @Nullable String typeName,
      @NonNull StaticContext staticContext) {
    return new KindFlagTestImpl(instanceName, typeName, staticContext);
  }

  /**
   * Matches an flag with any name and a type matching the provided name of a
   * specific globally-scoped flag definition.
   *
   * @param typeName
   *          the name of the globally-scoped flag definition to match
   * @param staticContext
   *          the static context in which the test was declared
   * @return the test
   */
  @NonNull
  static IKindTest<IFlagNodeItem> flag(@NonNull String typeName, @NonNull StaticContext staticContext) {
    return new KindFlagTestImpl(null, typeName, staticContext);
  }

  default boolean isInstance(IItem item) {
    return getItemClass().isInstance(item);
  }

  @NonNull
  Class<? extends IItem> getItemClass();

  @NonNull
  String toSignature();
}
