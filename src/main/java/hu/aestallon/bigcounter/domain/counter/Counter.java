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

package hu.aestallon.bigcounter.domain.counter;


import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonPropertyOrder({
    Counter.URI,
    Counter.NAME,
    Counter.VALUE
})
@JsonTypeName("Counter")
public final class Counter {

  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String VALUE = "value";
  private Long value;

  public Counter() {}

  public Counter uri(final URI uri) {
    this.uri = Objects.requireNonNull(uri, "uri must not be null!");
    return this;
  }

  @JsonProperty(URI)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public URI getUri() {
    return uri;
  }

  @JsonProperty(URI)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public void setUri(java.net.URI uri) {
    this.uri = uri;
  }

  public Counter name(final String name) {
    this.name = Objects.requireNonNull(name, "name must not be null!");
    return this;
  }

  @JsonProperty(NAME)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public String getName() {
    return name;
  }

  @JsonProperty(NAME)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }

  public Counter value(final long value) {
    this.value = value;
    return this;
  }

  @JsonProperty(VALUE)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public Long getValue() {
    return value;
  }


  @JsonProperty(VALUE)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public void setValue(Long value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Counter counter = (Counter) o;
    return Objects.equals(uri, counter.uri) && Objects.equals(name, counter.name)
        && Objects.equals(value, counter.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, value);
  }

  @Override
  public String toString() {
    return "Counter {" + "\n" +
        "  uri: " + uri + "\n" +
        "  name: " + name + "\n" +
        "  value: " + value + "\n" +
        '}';
  }

}
