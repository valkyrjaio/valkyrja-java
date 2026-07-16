/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.metadata.contract;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * A case-insensitive multi-map of metadata keys to lists of string-or-binary values.
 *
 * <p>Represents both HTTP/2 headers (request metadata, initial response metadata) and HTTP/2
 * trailing headers (trailing response metadata). Keys ending in {@code -bin} carry binary values
 * (base64-encoded on the wire, decoded at the library boundary); all other keys carry string
 * values. The value type is therefore the union {@code String | byte[]}, modeled here as {@link
 * Object}.
 */
public interface MetadataContract extends Iterable<Map.Entry<String, List<Object>>> {

    /**
     * Get the first value for a key.
     *
     * @param key the key (case-insensitive)
     * @return the first value ({@link String} or {@code byte[]}), or null if the key is absent
     */
    @Nullable Object get(String key);

    /**
     * Get all values for a key.
     *
     * @param key the key (case-insensitive)
     * @return the values ({@link String} or {@code byte[]}); empty if the key is absent
     */
    List<Object> getAll(String key);

    /**
     * Whether the key is present.
     *
     * @param key the key (case-insensitive)
     * @return true if present
     */
    boolean has(String key);

    /**
     * Whether the key names a binary value (ends in {@code -bin}).
     *
     * @param key the key (case-insensitive)
     * @return true if the key carries binary values
     */
    boolean isBinaryKey(String key);

    /**
     * Return a copy with the key set to a single value, replacing any existing values.
     *
     * @param key the key (case-insensitive)
     * @param value the value ({@link String} or {@code byte[]})
     * @return a new metadata
     */
    MetadataContract with(String key, Object value);

    /**
     * Return a copy with the value appended to any existing values for the key.
     *
     * @param key the key (case-insensitive)
     * @param value the value ({@link String} or {@code byte[]})
     * @return a new metadata
     */
    MetadataContract withAdded(String key, Object value);

    /**
     * Return a copy with the key removed.
     *
     * @param key the key (case-insensitive)
     * @return a new metadata
     */
    MetadataContract without(String key);

    /**
     * Get an immutable snapshot as a map of lower-cased keys to value lists.
     *
     * @return the map view
     */
    Map<String, List<Object>> toMap();
}
