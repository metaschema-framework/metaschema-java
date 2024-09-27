
package gov.nist.secauto.metaschema.core.metapath.cst.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArraytestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AtomicoruniontypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.FunctiontestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ItemtypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MaptestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.OccurrenceindicatorContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ParenthesizeditemtypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SequencetypeContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10Lexer;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class TypeTestSupport {

  private static final Map<
      Class<? extends ParseTree>,
      ParseTreeParser<IItemType>> ITEM_TYPE_HANDLER_MAP
          = Map.of(FunctiontestContext.class, TypeTestSupport::parseFunctionTest,
              MaptestContext.class, TypeTestSupport::parseMapTest,
              ArraytestContext.class, TypeTestSupport::parseArrayTest,
              AtomicoruniontypeContext.class, TypeTestSupport::parseAtomicType,
              ParenthesizeditemtypeContext.class, TypeTestSupport::parseParenthesizedItemType);

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
  private static IItemType parseFunctionTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    FunctiontestContext ctx = (FunctiontestContext) tree;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseMapTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    MaptestContext ctx = (MaptestContext) tree;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseArrayTest(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    ArraytestContext ctx = (ArraytestContext) tree;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseAtomicType(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    AtomicoruniontypeContext ctx = (AtomicoruniontypeContext) tree;
    throw new UnsupportedOperationException("implement");
  }

  @NonNull
  private static IItemType parseParenthesizedItemType(
      @NonNull ParseTree tree,
      @NonNull StaticContext staticContext) {
    ParenthesizeditemtypeContext ctx = (ParenthesizeditemtypeContext) tree;
    return parseItemType(ctx.itemtype(), staticContext);
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
