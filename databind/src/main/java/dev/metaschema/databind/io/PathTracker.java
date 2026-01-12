/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import java.util.ArrayDeque;
import java.util.Deque;

import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A lightweight utility for tracking the current path during parsing.
 * <p>
 * This class maintains a stack of path segments that can be pushed and popped
 * as the parser descends into and ascends from nested elements. The current
 * path can be retrieved at any time as a formatted string.
 * <p>
 * Path format:
 * <ul>
 * <li>Empty stack: "/" (root)</li>
 * <li>Single element: "/element"</li>
 * <li>Nested elements: "/parent/child/grandchild"</li>
 * </ul>
 * <p>
 * This class is not thread-safe and should be used within a single parsing
 * context.
 */
public class PathTracker {
  private final Deque<String> segments;

  /**
   * Construct a new empty path tracker.
   */
  public PathTracker() {
    this.segments = new ArrayDeque<>();
  }

  /**
   * Push a new segment onto the path.
   *
   * @param segment
   *          the segment name to add, must not be null
   */
  public void push(@NonNull String segment) {
    segments.push(segment);
  }

  /**
   * Pop the most recent segment from the path.
   *
   * @return the removed segment, or null if the path was empty
   */
  @Nullable
  public String pop() {
    return segments.poll();
  }

  /**
   * Get the current path as a formatted string.
   * <p>
   * Returns "/" for an empty path, or "/segment1/segment2/..." for nested paths.
   *
   * @return the current path string
   */
  @NonNull
  public String getCurrentPath() {
    if (segments.isEmpty()) {
      return "/";
    }
    // Deque iterates from top (most recent) to bottom, so we need to reverse
    StringBuilder sb = new StringBuilder();
    // Convert to list and reverse to get correct order
    Object[] arr = segments.toArray();
    for (int i = arr.length - 1; i >= 0; i--) {
      sb.append('/').append(arr[i]);
    }
    return ObjectUtils.notNull(sb.toString());
  }

  /**
   * Get the depth of the current path (number of segments).
   *
   * @return the number of segments in the path
   */
  public int getDepth() {
    return segments.size();
  }

  /**
   * Check if the path is empty (at root level).
   *
   * @return true if the path has no segments
   */
  public boolean isEmpty() {
    return segments.isEmpty();
  }

  /**
   * Clear all segments from the path.
   */
  public void clear() {
    segments.clear();
  }

  @Override
  public String toString() {
    return getCurrentPath();
  }
}
