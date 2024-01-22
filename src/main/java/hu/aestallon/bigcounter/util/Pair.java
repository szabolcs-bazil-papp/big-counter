/*
 * Copyright (C) 2024 it4all Hungary Kft.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package hu.aestallon.bigcounter.util;

import java.util.Map;
import java.util.Objects;

public final class Pair<A, B> {

  public static <A, B> Pair<A, B> of(A a, B b) {
    return new Pair<>(a, b);
  }

  public static <A, B> Pair<A, B> of(Map.Entry<A, B> e) {
    return of(e.getKey(), e.getValue());
  }

  private final A a;
  private final B b;

  private Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  public A a() {
    return a;
  }

  public B b() {
    return b;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
  }

  @Override
  public int hashCode() {
    return Objects.hash(a, b);
  }

  @Override
  public String toString() {
    return "Pair { " +
        "a: " + a +
        ", b: " + b +
        " }";
  }

}
