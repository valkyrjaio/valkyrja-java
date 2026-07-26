/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.metadata;

import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.throwable.exception.MetadataInvalidKeyException;
import io.valkyrja.grpc.throwable.exception.MetadataInvalidValueException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Immutable {@link MetadataContract} implementation backed by an insertion-ordered map of
 * lower-cased keys to value lists.
 *
 * <p>Keys are compared case-insensitively; the {@code -bin} suffix convention marks binary values.
 * Every {@code with*}/{@code without} operation returns a fresh instance.
 */
public class Metadata implements MetadataContract {

    protected static final String BINARY_SUFFIX = "-bin";

    /**
     * The valid gRPC metadata key charset (matched against the normalized, lower-cased key): one or
     * more of a lowercase letter, digit, {@code -}, {@code _}, or {@code .}. Mirrors the set gRPC
     * itself enforces, so a key accepted here is one the transport will accept.
     */
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9._-]+");

    protected final Map<String, List<Object>> values;

    public Metadata() {
        this(new LinkedHashMap<>());
    }

    public Metadata(Map<String, List<Object>> values) {
        Map<String, List<Object>> copy = new LinkedHashMap<>();

        for (Map.Entry<String, List<Object>> entry : values.entrySet()) {
            String key = normalize(entry.getKey());
            validateKey(key);
            List<Object> validated = new ArrayList<>();
            for (Object value : entry.getValue()) {
                validateValue(key, value);
                validated.add(value);
            }
            copy.put(key, validated);
        }

        this.values = copy;
    }

    /**
     * Reject a key that is not a valid gRPC header name at the point of insertion — as HTTP does
     * for header names — so a malformed key fails fast in the handler rather than throwing an
     * opaque {@code IllegalArgumentException} from deep in the wire write when the response is
     * sent.
     *
     * @param normalizedKey the already-normalized key
     * @throws MetadataInvalidKeyException if the key is empty or contains characters outside the
     *     gRPC key charset
     */
    private static void validateKey(String normalizedKey) {
        if (!VALID_KEY.matcher(normalizedKey).matches()) {
            throw new MetadataInvalidKeyException(
                    "'"
                            + normalizedKey
                            + "' is not a valid metadata key; keys may contain only lowercase"
                            + " letters, digits, '-', '_', and '.'.");
        }
    }

    /**
     * Enforce the metadata value union at the boundary: a {@code -bin} key carries {@code byte[]},
     * every other key carries a {@code String}. Validating on construction (the single point every
     * {@code with*} operation flows through) means {@code toGrpcMetadata} can trust the types
     * instead of a mismatched value throwing a {@code ClassCastException} — or silently sending a
     * {@code "[B@…"} array toString — deep inside the wire write.
     *
     * @param normalizedKey the already-normalized key
     * @param value the value to validate
     * @throws MetadataInvalidValueException if the value type does not match the key's kind
     */
    private static void validateValue(String normalizedKey, @Nullable Object value) {
        if (normalizedKey.endsWith(BINARY_SUFFIX)) {
            if (!(value instanceof byte[])) {
                throw new MetadataInvalidValueException(
                        "Binary metadata key '"
                                + normalizedKey
                                + "' requires a byte[] value, but got "
                                + typeName(value)
                                + ".");
            }
        } else if (!(value instanceof String)) {
            throw new MetadataInvalidValueException(
                    "ASCII metadata key '"
                            + normalizedKey
                            + "' requires a String value, but got "
                            + typeName(value)
                            + "; use a '-bin' suffixed key to carry binary values.");
        }
    }

    private static String typeName(@Nullable Object value) {
        return value == null ? "null" : value.getClass().getName();
    }

    @Override
    public @Nullable Object get(String key) {
        List<Object> all = values.get(normalize(key));

        return all == null || all.isEmpty() ? null : all.get(0);
    }

    @Override
    public List<Object> getAll(String key) {
        List<Object> all = values.get(normalize(key));

        return all == null ? List.of() : Collections.unmodifiableList(all);
    }

    @Override
    public boolean has(String key) {
        return values.containsKey(normalize(key));
    }

    @Override
    public boolean isBinaryKey(String key) {
        return normalize(key).endsWith(BINARY_SUFFIX);
    }

    @Override
    public MetadataContract with(String key, Object value) {
        Map<String, List<Object>> copy = copyValues();
        List<Object> list = new ArrayList<>();
        list.add(value);
        copy.put(normalize(key), list);

        return new Metadata(copy);
    }

    @Override
    public MetadataContract withAdded(String key, Object value) {
        Map<String, List<Object>> copy = copyValues();
        copy.computeIfAbsent(normalize(key), k -> new ArrayList<>()).add(value);

        return new Metadata(copy);
    }

    @Override
    public MetadataContract without(String key) {
        Map<String, List<Object>> copy = copyValues();
        copy.remove(normalize(key));

        return new Metadata(copy);
    }

    @Override
    public Map<String, List<Object>> toMap() {
        Map<String, List<Object>> snapshot = new LinkedHashMap<>();

        for (Map.Entry<String, List<Object>> entry : values.entrySet()) {
            snapshot.put(
                    entry.getKey(),
                    Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }

        return Collections.unmodifiableMap(snapshot);
    }

    @Override
    public Iterator<Map.Entry<String, List<Object>>> iterator() {
        return toMap().entrySet().iterator();
    }

    protected Map<String, List<Object>> copyValues() {
        Map<String, List<Object>> copy = new LinkedHashMap<>();

        for (Map.Entry<String, List<Object>> entry : values.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return copy;
    }

    protected static String normalize(String key) {
        return key.toLowerCase(Locale.ROOT);
    }
}
