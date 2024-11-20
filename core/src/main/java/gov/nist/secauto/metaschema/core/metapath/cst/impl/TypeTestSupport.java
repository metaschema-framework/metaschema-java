/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AnykindtestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArraytestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AssemblytestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AtomicoruniontypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.DocumenttestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.FieldtestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.FlagnameorwildcardContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.FlagtestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.FunctiontestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ItemtypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.KindtestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MaptestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.OccurrenceindicatorContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ParenthesizeditemtypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SequencetypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.TypedarraytestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.TypedmaptestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.Typename_Context;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10Lexer;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class TypeTestSupport {

  private static final Map<
      Class<? extends ParseTree>,
      ParseTreeParser<IItemType>> ITEM_TYPE_HANDLER_MAP
          = Map.of(
              KindtestContext.class, TypeTestSupport::parseKindTest,
              FunctiontestContext.class, TypeTestSupport::parseFunctionTest,
              MaptestContext.class, TypeTestSupport::parseMapTest,
              ArraytestContext.class, TypeTestSupport::parseArrayTest,
              AtomicoruniontypeContext.class, TypeTestSupport::parseAtomicType,
              ParenthesizeditemtypeContext.class, TypeTestSupport::parseParenthesizedItemType);
  private static final Map<
      Class<? extends ParseTree>,
      ParseTreeParser<IItemType>> KIND_TEST_HANDLER_MAP
          = Map.of(
              DocumenttestContext.class, TypeTestSupport::parseKindDocumentTest,
              FieldtestContext.class, TypeTestSupport::parseKindFieldTest,
              AssemblytestContext.class, TypeTestSupport::parseKindAssemblyTest,
              FlagtestContext.class, TypeTestSupport::parseKindFlagTest,
              AnykindtestContext.class, TypeTestSupport::parseKindAny);

  @NonNull
  public static ISequenceType parseSequenceType(
      @NonNull SequencetypeContext ctx,
      @NonNull StaticContext staticContext) {
    return ctx.KW_EMPTY_SEQUENCE() == null
        ? parseSequenceType(ctx.itemtype(), ctx.occurrenceindicator(), staticContext)
        : ISequenceType.empty();
  }

  private static ISequenceType parseSequenceType(
      @NonNull ItemtypeContext itemTypeCtx,
      @NonNull OccurrenceindicatorContext occurrenceIndicatorCtx,
      @NonNull StaticContext staticContext) {
    IItemType itemType = parseItemType(itemTypeCtx, staticContext);
    Occurrence occurrence = parseOccurrence(occurrenceIndicatorCtx);
    return ISequenceType.of(itemType, occurrence);
  }

  @NonNull
  private static IItemType parseItemType(
      @NonNull ItemtypeContext ctx,
      @NonNull StaticContext staticContext) {
    IItemType retval;
    if (ctx.KW_ITEM() != null) {
      retval = IItemType.item();
    } else {
      ParseTree tree = ctx.getChild(0);
      retval = ITEM_TYPE_HANDLER_MAP.get(tree.getClass()).parse(tree, staticContext);
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
  private static IItemType parseKindDocumentTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    DocumenttestContext ctx = (DocumenttestContext) tree;
    assert ctx != null;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseKindFieldTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    FieldtestContext ctx = (FieldtestContext) tree;
    assert ctx != null;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseKindAssemblyTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    AssemblytestContext ctx = (AssemblytestContext) tree;
    assert ctx != null;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseKindFlagTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    FlagtestContext ctx = (FlagtestContext) tree;

    FlagnameorwildcardContext nameOrWildcard = ctx.flagnameorwildcard();
    IItemType retval;
    if (nameOrWildcard == null) {
      retval = IItemType.flag();
    } else {
      IEnhancedQName name = nameOrWildcard.STAR() == null
          // eqname
          ? staticContext.parseFlagName(nameOrWildcard.flagname().eqname().getText())
          // STAR
          : null;

      IAtomicOrUnionType dataType = null;
      Typename_Context typeName = ctx.typename_();
      if (typeName != null) {
        String dataTypeName = ObjectUtils.notNull(typeName.eqname().getText());
        dataType = staticContext.lookupDataTypeItemType(dataTypeName);
      }

      retval = IItemType.flag(name, dataType);
    }
    return retval;
  }

  @NonNull
  private static IItemType parseKindAny(
      @NonNull ParseTree tree,
      @SuppressWarnings("unused") @NonNull StaticContext staticContext) {
    AnykindtestContext ctx = (AnykindtestContext) tree;
    assert ctx != null;
    return IItemType.node();
  }

  @NonNull
  private static IItemType parseFunctionTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    FunctiontestContext ctx = (FunctiontestContext) tree;
    assert ctx != null;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseMapTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    MaptestContext ctx = (MaptestContext) tree;

    ctx.anymaptest();
    ctx.typedmaptest();

    IItemType retval;
    if (ctx.anymaptest() != null) {
      retval = IItemType.map();
    } else {
      TypedmaptestContext typedMapCtx = ctx.typedmaptest();

      String dataTypeName = ObjectUtils.notNull(typedMapCtx.atomicoruniontype().getText());
      IAtomicOrUnionType dataType = staticContext.lookupDataTypeItemType(dataTypeName);
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
    ArraytestContext ctx = (ArraytestContext) tree;
    IItemType retval;
    if (ctx.anyarraytest() != null) {
      retval = IItemType.array();
    } else {
      TypedarraytestContext typedArrayCtx = ctx.typedarraytest();
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
    AtomicoruniontypeContext ctx = (AtomicoruniontypeContext) tree;

    String name = ObjectUtils.notNull(ctx.eqname().getText());
    return staticContext.lookupDataTypeItemType(name);
  }

  @NonNull
  private static IItemType parseParenthesizedItemType(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    ParenthesizeditemtypeContext ctx = (ParenthesizeditemtypeContext) tree;
    return parseItemType(ObjectUtils.notNull(ctx.itemtype()), staticContext);
  }

  @NonNull
  public static Occurrence parseOccurrence(@Nullable OccurrenceindicatorContext ctx) {
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
