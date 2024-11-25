/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10Lexer;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides parsing utility methods for sequence and type tests.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class TypeTestSupport {

  private static final Map<
      Class<? extends ParseTree>,
      ParseTreeParser<IItemType>> ITEM_TYPE_HANDLER_MAP
          = Map.of(
              Metapath10.KindtestContext.class, TypeTestSupport::parseKindTest,
              Metapath10.FunctiontestContext.class, TypeTestSupport::parseFunctionTest,
              Metapath10.MaptestContext.class, TypeTestSupport::parseMapTest,
              Metapath10.ArraytestContext.class, TypeTestSupport::parseArrayTest,
              Metapath10.AtomicoruniontypeContext.class, TypeTestSupport::parseAtomicType,
              Metapath10.ParenthesizeditemtypeContext.class, TypeTestSupport::parseParenthesizedItemType);
  private static final Map<
      Class<? extends ParseTree>,
      ParseTreeParser<? extends IKindTest<?>>> KIND_TEST_HANDLER_MAP
          = Map.of(
              Metapath10.DocumenttestContext.class, TypeTestSupport::parseKindDocumentTest,
              Metapath10.FieldtestContext.class, TypeTestSupport::parseKindFieldTest,
              Metapath10.AssemblytestContext.class, TypeTestSupport::parseKindAssemblyTest,
              Metapath10.FlagtestContext.class, TypeTestSupport::parseKindFlagTest,
              Metapath10.AnykindtestContext.class, TypeTestSupport::parseKindAny);

  /**
   * Parse the provided sequence type.
   *
   * @param ctx
   *          the sequence type parse tree
   * @param staticContext
   *          the static context used to resolve types and other information
   * @return the parse sequence type
   */
  @NonNull
  public static ISequenceType parseSequenceType(
      @NonNull Metapath10.SequencetypeContext ctx,
      @NonNull StaticContext staticContext) {
    return ObjectUtils.notNull(ctx.KW_EMPTY_SEQUENCE() == null
        ? parseSequenceType(
            ObjectUtils.requireNonNull(ctx.itemtype()),
            ctx.occurrenceindicator(),
            staticContext)
        : ISequenceType.empty());
  }

  private static ISequenceType parseSequenceType(
      @NonNull Metapath10.ItemtypeContext itemTypeCtx,
      @Nullable Metapath10.OccurrenceindicatorContext occurrenceIndicatorCtx,
      @NonNull StaticContext staticContext) {
    IItemType itemType = parseItemType(itemTypeCtx, staticContext);
    Occurrence occurrence = parseOccurrence(occurrenceIndicatorCtx);
    return ISequenceType.of(itemType, occurrence);
  }

  @NonNull
  private static IItemType parseItemType(
      @NonNull Metapath10.ItemtypeContext ctx,
      @NonNull StaticContext staticContext) {
    IItemType retval;
    if (ctx.KW_ITEM() == null) {
      ParseTree tree = ctx.getChild(0);
      retval = ITEM_TYPE_HANDLER_MAP.get(tree.getClass()).parse(tree, staticContext);
    } else {
      retval = IItemType.item();
    }
    return retval;
  }

  @NonNull
  private static IItemType parseKindTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    ParseTree child = ObjectUtils.requireNonNull(tree.getChild(0));
    return KIND_TEST_HANDLER_MAP.get(child.getClass()).parse(child, staticContext);
  }

  @NonNull
  private static IKindTest<IDocumentNodeItem> parseKindDocumentTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.DocumenttestContext ctx = (Metapath10.DocumenttestContext) tree;

    return ctx.assemblytest() == null
        ? IItemType.document()
        : IItemType.document(parseKindAssemblyTest(ObjectUtils.notNull(ctx.assemblytest()), staticContext));
  }

  @NonNull
  private static IKindTest<IAssemblyNodeItem> parseKindAssemblyTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {

    Metapath10.AssemblytestContext ctx = (Metapath10.AssemblytestContext) tree;
    IEnhancedQName instanceName
        = ctx.elementnameorwildcard() == null || ctx.elementnameorwildcard().elementname() == null
            ? null
            : staticContext.parseModelName(ObjectUtils.notNull(
                ctx.elementnameorwildcard().elementname().eqname().getText()));
    String typeName = ctx.typename_() == null ? null : ctx.typename_().eqname().getText();
    return instanceName == null
        ? typeName == null
            ? IItemType.assembly()
            : IItemType.assembly(typeName, staticContext)
        : IItemType.assembly(instanceName, typeName, staticContext);
  }

  @NonNull
  private static IKindTest<IFieldNodeItem> parseKindFieldTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.FieldtestContext ctx = (Metapath10.FieldtestContext) tree;
    IEnhancedQName instanceName
        = ctx.elementnameorwildcard() == null || ctx.elementnameorwildcard().elementname() == null
            ? null
            : staticContext.parseModelName(ObjectUtils.notNull(
                ctx.elementnameorwildcard().elementname().eqname().getText()));
    String typeName = ctx.typename_() == null ? null : ctx.typename_().eqname().getText();
    return instanceName == null
        ? typeName == null
            ? IItemType.field()
            : IItemType.field(typeName, staticContext)
        : IItemType.field(instanceName, typeName, staticContext);
  }

  @NonNull
  private static IKindTest<IFlagNodeItem> parseKindFlagTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.FlagtestContext ctx = (Metapath10.FlagtestContext) tree;
    IEnhancedQName instanceName
        = ctx.flagnameorwildcard() == null || ctx.flagnameorwildcard().flagname() == null
            ? null
            : staticContext.parseFlagName(ObjectUtils.notNull(
                ctx.flagnameorwildcard().flagname().eqname().getText()));
    String typeName = ctx.typename_() == null ? null : ctx.typename_().eqname().getText();
    return instanceName == null
        ? typeName == null
            ? IItemType.flag()
            : IItemType.flag(typeName, staticContext)
        : IItemType.flag(instanceName, typeName, staticContext);
  }

  @SuppressWarnings("unused")
  @NonNull
  private static IKindTest<INodeItem> parseKindAny(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    return IItemType.node();
  }

  @SuppressWarnings("unused")
  @NonNull
  private static IItemType parseFunctionTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.FunctiontestContext ctx = (Metapath10.FunctiontestContext) tree;
    assert ctx != null;
    throw new UnsupportedOperationException("expression not supported");

  }

  @NonNull
  private static IItemType parseMapTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.MaptestContext ctx = (Metapath10.MaptestContext) tree;

    ctx.anymaptest();
    ctx.typedmaptest();

    IItemType retval;
    if (ctx.anymaptest() != null) {
      retval = IItemType.map();
    } else {
      Metapath10.TypedmaptestContext typedMapCtx = ctx.typedmaptest();

      String dataTypeName = ObjectUtils.notNull(typedMapCtx.atomicoruniontype().getText());
      IAtomicOrUnionType<?> dataType = staticContext.lookupAtomicType(dataTypeName);
      retval = IItemType.map(
          dataType,
          parseSequenceType(ObjectUtils.requireNonNull(typedMapCtx.sequencetype()), staticContext));
    }
    return retval;
  }

  @NonNull
  private static IItemType parseArrayTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.ArraytestContext ctx = (Metapath10.ArraytestContext) tree;
    IItemType retval;
    if (ctx.anyarraytest() != null) {
      retval = IItemType.array();
    } else {
      Metapath10.TypedarraytestContext typedArrayCtx = ctx.typedarraytest();
      retval = IItemType.array(parseSequenceType(
          ObjectUtils.notNull(typedArrayCtx.sequencetype()),
          staticContext));
    }
    return retval;
  }

  @NonNull
  private static IItemType parseAtomicType(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.AtomicoruniontypeContext ctx = (Metapath10.AtomicoruniontypeContext) tree;

    String name = ObjectUtils.notNull(ctx.eqname().getText());
    return staticContext.lookupAtomicType(name);
  }

  @NonNull
  private static IItemType parseParenthesizedItemType(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    Metapath10.ParenthesizeditemtypeContext ctx = (Metapath10.ParenthesizeditemtypeContext) tree;
    return parseItemType(ObjectUtils.notNull(ctx.itemtype()), staticContext);
  }

  /**
   * Parse the occurrence indicator.
   *
   * @param ctx
   *          the occurrence parse tree
   * @return the parsed occurrence value
   */
  @NonNull
  public static Occurrence parseOccurrence(@Nullable Metapath10.OccurrenceindicatorContext ctx) {
    Occurrence retval;
    if (ctx == null) {
      retval = Occurrence.ONE;
    } else {
      int type = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
      switch (type) {
      case Metapath10Lexer.QM:
        retval = Occurrence.ZERO_OR_ONE;
        break;
      case Metapath10Lexer.STAR:
        retval = Occurrence.ZERO_OR_MORE;
        break;
      case Metapath10Lexer.PLUS:
        retval = Occurrence.ONE_OR_MORE;
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) ctx.getChild(0)).getSymbol().getText());
      }
    }
    return retval;
  }

  @FunctionalInterface
  private interface ParseTreeParser<R> {
    @NonNull
    R parse(@NonNull ParseTree parseTree, @NonNull StaticContext staticContext);
  }

  private TypeTestSupport() {
    // disable construction
  }

}
