/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;

import java.util.Collections;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides the ability to suppress paragraph "p" tags for single line markup
 * generation.
 */
public class SuppressPTagExtension
    implements HtmlRenderer.HtmlRendererExtension {

  /**
   * Construct a new extension instance.
   *
   * @return the instance
   */
  public static SuppressPTagExtension newInstance() {
    return new SuppressPTagExtension();
  }

  @Override
  public void rendererOptions(MutableDataHolder options) {
    // do nothing
  }

  @Override
  public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
    rendererBuilder.nodeRendererFactory(new PTagNodeRenderer.Factory());
  }

  static class PTagNodeRenderer implements NodeRenderer {

    @Override
    public @Nullable
    Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
      return Collections.singleton(
          new NodeRenderingHandler<>(Paragraph.class, this::render));
    }

    protected void render(
        @NonNull Paragraph node,
        @NonNull NodeRendererContext context,
        @SuppressWarnings("unused") @NonNull HtmlWriter html) {
      context.renderChildren(node);
    }

    public static class Factory implements NodeRendererFactory {

      @Override
      public NodeRenderer apply(DataHolder options) {
        return new PTagNodeRenderer();
      }

    }
  }

}
