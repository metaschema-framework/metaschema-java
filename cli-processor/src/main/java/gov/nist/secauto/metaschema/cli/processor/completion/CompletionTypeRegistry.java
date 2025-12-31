/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Registry for mapping Java types to their shell completion behavior.
 * <p>
 * This decouples type classes from completion logic, allowing any type to have
 * completion behavior registered without implementing an interface.
 * <p>
 * Built-in types ({@link File}, {@link URI}, {@link URL}) are pre-registered.
 * Custom types can be registered using
 * {@link #register(Class, ICompletionType)} or {@link #registerEnum(Class)} for
 * enum types.
 */
public final class CompletionTypeRegistry {
  private static final Map<Class<?>, ICompletionType> REGISTRY = new ConcurrentHashMap<>();

  // Pre-register built-in types
  static {
    register(File.class, new FileCompletionType());
    register(URI.class, new UriCompletionType());
    register(URL.class, new UriCompletionType());
  }

  private CompletionTypeRegistry() {
    // static utility class
  }

  /**
   * Register a completion type for a class.
   *
   * @param type
   *          the type class to register
   * @param completion
   *          the completion behavior for that type
   */
  public static void register(@NonNull Class<?> type, @NonNull ICompletionType completion) {
    REGISTRY.put(type, completion);
  }

  /**
   * Register an enum type using its constant names as completion values.
   * <p>
   * Enum names are converted to lowercase for completion.
   *
   * @param <E>
   *          the enum type
   * @param enumClass
   *          the enum class to register
   */
  public static <E extends Enum<E>> void registerEnum(@NonNull Class<E> enumClass) {
    register(enumClass, forEnum(enumClass));
  }

  /**
   * Create a completion type for an enum using its constant names.
   * <p>
   * Enum names are converted to lowercase for completion.
   *
   * @param <E>
   *          the enum type
   * @param enumClass
   *          the enum class
   * @return completion type that offers enum values
   */
  @NonNull
  public static <E extends Enum<E>> ICompletionType forEnum(@NonNull Class<E> enumClass) {
    String values = ObjectUtils.notNull(Arrays.stream(enumClass.getEnumConstants())
        .map(Enum::name)
        .map(String::toLowerCase)
        .collect(Collectors.joining(" ")));

    return new EnumCompletionType(values);
  }

  /**
   * Lookup the completion type for a class.
   *
   * @param type
   *          the type class to lookup
   * @return the registered completion type, or {@code null} if none registered
   */
  @Nullable
  public static ICompletionType lookup(@Nullable Class<?> type) {
    return type == null ? null : REGISTRY.get(type);
  }

  /**
   * Built-in completion type for file paths.
   */
  private static final class FileCompletionType implements ICompletionType {
    @Override
    public String getBashCompletion() {
      return "_filedir";
    }

    @Override
    public String getZshCompletion() {
      return "_files";
    }
  }

  /**
   * Built-in completion type for URIs/URLs.
   */
  private static final class UriCompletionType implements ICompletionType {
    @Override
    public String getBashCompletion() {
      return ""; // freeform in bash
    }

    @Override
    public String getZshCompletion() {
      return "_urls";
    }
  }

  /**
   * Completion type for enum values.
   */
  private static final class EnumCompletionType implements ICompletionType {
    @NonNull
    private final String values;

    EnumCompletionType(@NonNull String values) {
      this.values = values;
    }

    @Override
    public String getBashCompletion() {
      return "compgen -W \"" + values + "\"";
    }

    @Override
    public String getZshCompletion() {
      return "(" + values + ")";
    }
  }
}
