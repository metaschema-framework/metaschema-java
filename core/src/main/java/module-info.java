/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

import dev.metaschema.core.datatype.IDataTypeProvider;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import dev.metaschema.core.metapath.function.IFunctionLibrary;
import dev.metaschema.core.metapath.function.library.DefaultFunctionLibrary;

/**
 * @provides IDataTypeProvider for core built-in data types
 * @provides IFunctionLibrary for core built-in Metapath functions
 * @uses IDataTypeProvider to discover data types implementing
 *       {@link dev.metaschema.core.datatype.IDataTypeAdapter}
 * @uses IFunctionLibrary to discover collections of Metapath functions
 *       implementing
 *       {@link dev.metaschema.core.metapath.function.IFunction}
 */
@SuppressWarnings("requires-transitive-automatic")
module dev.metaschema.core {
  // requirements
  requires java.base;
  requires java.xml;

  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;

  requires com.ctc.wstx;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
  requires transitive com.fasterxml.jackson.databind;
  requires transitive com.github.benmanes.caffeine;
  requires transitive inet.ipaddr;
  requires nl.talsmasoftware.lazy4j;
  requires org.antlr.antlr4.runtime;
  requires org.apache.commons.codec;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.logging.log4j;
  requires transitive org.codehaus.stax2;
  requires transitive org.json;
  requires org.jsoup;

  // dependencies without a module descriptor (automatic modules)
  requires transitive everit.json.schema;
  requires transitive flexmark;
  requires flexmark.ext.escaped.character;
  requires flexmark.ext.gfm.strikethrough;
  requires flexmark.ext.superscript;
  requires transitive flexmark.ext.tables;
  requires transitive flexmark.ext.typographic;
  requires transitive flexmark.html2md.converter;
  requires transitive flexmark.util.ast;
  requires flexmark.util.builder;
  requires flexmark.util.collection;
  requires transitive flexmark.util.data;
  requires flexmark.util.dependency;
  requires flexmark.util.format;
  requires flexmark.util.html;
  requires flexmark.util.misc;
  requires transitive flexmark.util.sequence;
  requires flexmark.util.visitor;

  exports dev.metaschema.core;
  exports dev.metaschema.core.configuration;
  exports dev.metaschema.core.datatype;
  exports dev.metaschema.core.datatype.adapter;
  exports dev.metaschema.core.datatype.markup;
  exports dev.metaschema.core.datatype.object;
  exports dev.metaschema.core.metapath;
  exports dev.metaschema.core.metapath.format;
  exports dev.metaschema.core.metapath.function;
  exports dev.metaschema.core.metapath.function.library;
  exports dev.metaschema.core.metapath.function.regex;
  exports dev.metaschema.core.metapath.item;
  exports dev.metaschema.core.metapath.item.atomic;
  exports dev.metaschema.core.metapath.item.function;
  exports dev.metaschema.core.metapath.item.node;
  exports dev.metaschema.core.metapath.type;
  exports dev.metaschema.core.model;
  exports dev.metaschema.core.model.constraint;
  exports dev.metaschema.core.model.util;
  exports dev.metaschema.core.model.validation;
  exports dev.metaschema.core.qname;
  exports dev.metaschema.core.util;

  exports dev.metaschema.core.datatype.markup.flexmark
      to dev.metaschema.databind;

  // make bundled schemas and related resources available for use
  opens schema.json;
  opens schema.xml;
  opens schema.metaschema;

  // allow reflection on data types
  opens dev.metaschema.core.datatype.markup;

  // services
  uses IDataTypeProvider;
  uses IFunctionLibrary;

  provides IFunctionLibrary with DefaultFunctionLibrary;
  provides IDataTypeProvider with MetaschemaDataTypeProvider, MarkupDataTypeProvider;
}
