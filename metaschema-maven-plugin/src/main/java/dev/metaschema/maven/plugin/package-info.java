/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides Maven plugin goals for Metaschema-based code and schema generation.
 * <p>
 * This package contains Maven plugin Mojos that integrate Metaschema processing
 * into Maven builds. The plugin supports generating Java binding classes and
 * schema files (XSD and JSON Schema) from Metaschema module definitions.
 * <p>
 * Available goals:
 * <ul>
 * <li>{@code generate-sources} - Generates Java source files from Metaschema
 * modules, bound to the {@code generate-sources} lifecycle phase</li>
 * <li>{@code generate-schemas} - Generates XML Schema (XSD) and/or JSON Schema
 * files from Metaschema modules, bound to the {@code generate-resources}
 * lifecycle phase</li>
 * </ul>
 * <p>
 * Key classes:
 * <ul>
 * <li>{@link dev.metaschema.maven.plugin.AbstractMetaschemaMojo} - Base class
 * providing common functionality for module loading, constraint handling, and
 * incremental build support</li>
 * <li>{@link dev.metaschema.maven.plugin.GenerateSourcesMojo} - Generates Java
 * binding classes from Metaschema modules</li>
 * <li>{@link dev.metaschema.maven.plugin.GenerateSchemaMojo} - Generates schema
 * files from Metaschema modules</li>
 * </ul>
 * <p>
 * Example plugin configuration:
 *
 * <pre>{@code
 * <plugin>
 *   <groupId>dev.metaschema</groupId>
 *   <artifactId>metaschema-maven-plugin</artifactId>
 *   <executions>
 *     <execution>
 *       <goals>
 *         <goal>generate-sources</goal>
 *       </goals>
 *     </execution>
 *   </executions>
 * </plugin>
 * }</pre>
 *
 * @see dev.metaschema.maven.plugin.GenerateSourcesMojo
 * @see dev.metaschema.maven.plugin.GenerateSchemaMojo
 */

package dev.metaschema.maven.plugin;
