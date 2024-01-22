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

package hu.aestallon.bigcounter.domain.command;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonPropertyOrder({
    IncrementCommand.URI,
    IncrementCommand.BATCH_SIZE
})
@JsonTypeName("IncrementCommand")
public final class IncrementCommand {

  public static final String URI = "uri";
  private URI uri;

  public static final String BATCH_SIZE = "batchSize";
  private Integer batchSize;

  public IncrementCommand() {}

  public IncrementCommand uri(final URI uri) {
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
  public void setUri(URI uri) {
    this.uri = uri;
  }

  public IncrementCommand batchSize(final int batchSize) {
    this.batchSize = batchSize;
    return this;
  }

  @JsonProperty(BATCH_SIZE)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public Integer getBatchSize() {
    return batchSize;
  }

  @JsonProperty(BATCH_SIZE)
  @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
  public void setBatchSize(Integer batchSize) {
    this.batchSize = batchSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    IncrementCommand that = (IncrementCommand) o;
    return Objects.equals(uri, that.uri) && Objects.equals(batchSize,
        that.batchSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, batchSize);
  }

  @Override
  public String toString() {
    return "IncrementCommand {" + "\n" +
        "  uri: " + uri + "\n" +
        "  batchSize: " + batchSize + "\n" +
        '}';
  }

}
