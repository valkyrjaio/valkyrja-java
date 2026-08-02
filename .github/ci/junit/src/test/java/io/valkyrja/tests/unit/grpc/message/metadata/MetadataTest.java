/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.message.metadata;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.throwable.exception.MetadataInvalidKeyException;
import io.valkyrja.grpc.throwable.exception.MetadataInvalidValueException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link Metadata} value type. */
final class MetadataTest {

    @Test
    void emptyByDefault() {
        Metadata metadata = new Metadata();
        assertFalse(metadata.has("x"));
        assertNull(metadata.get("x"));
        assertTrue(metadata.getAll("x").isEmpty());
    }

    @Test
    void keysAreCaseInsensitive() {
        MetadataContract metadata = new Metadata().with("Content-Type", "application/grpc");
        assertEquals("application/grpc", metadata.get("content-type"));
        assertEquals("application/grpc", metadata.get("CONTENT-TYPE"));
        assertTrue(metadata.has("Content-Type"));
    }

    @Test
    void withReplacesExistingValues() {
        MetadataContract metadata =
                new Metadata().withAdded("k", "a").withAdded("k", "b").with("k", "c");
        assertEquals(List.of("c"), metadata.getAll("k"));
        assertEquals("c", metadata.get("k"));
    }

    @Test
    void withAddedAppends() {
        MetadataContract metadata = new Metadata().withAdded("k", "a").withAdded("k", "b");
        assertEquals(List.of("a", "b"), metadata.getAll("k"));
        assertEquals("a", metadata.get("k"));
    }

    @Test
    void without() {
        MetadataContract metadata = new Metadata().with("k", "a").without("K");
        assertFalse(metadata.has("k"));
    }

    @Test
    void isBinaryKey() {
        Metadata metadata = new Metadata();
        assertTrue(metadata.isBinaryKey("trace-bin"));
        assertTrue(metadata.isBinaryKey("Trace-Bin"));
        assertFalse(metadata.isBinaryKey("trace"));
    }

    @Test
    void binaryValuesAreSupported() {
        byte[] value = {1, 2, 3};
        MetadataContract metadata = new Metadata().with("trace-bin", value);
        Object stored = metadata.get("trace-bin");
        assertTrue(stored instanceof byte[]);
        assertArrayEquals(value, (byte[]) stored);
    }

    @Test
    void binaryKeyRejectsANonByteArrayValue() {
        // A String under a -bin key would later throw a ClassCastException deep in the wire write;
        // reject it at the boundary instead.
        MetadataInvalidValueException thrown =
                assertThrows(
                        MetadataInvalidValueException.class,
                        () -> new Metadata().with("trace-bin", "not-bytes"));
        assertTrue(thrown.getMessage().contains("byte[]"));
    }

    @Test
    void asciiKeyRejectsAByteArrayValue() {
        // A byte[] under a non -bin key would otherwise be sent to the client as its array
        // toString.
        MetadataInvalidValueException thrown =
                assertThrows(
                        MetadataInvalidValueException.class,
                        () -> new Metadata().withAdded("trace", new byte[] {1, 2}));
        assertTrue(thrown.getMessage().contains("-bin"));
    }

    @Test
    void asciiKeyRejectsANonStringValue() {
        assertThrows(MetadataInvalidValueException.class, () -> new Metadata().with("count", 42));
    }

    @Test
    void aNullValueIsRejected() {
        assertThrows(MetadataInvalidValueException.class, () -> new Metadata().with("k", null));
    }

    @Test
    void rejectsAKeyWithInvalidCharactersAtInsertion() {
        // A space is not a valid gRPC key char; fail fast here, not when writing the response.
        MetadataInvalidKeyException thrown =
                assertThrows(
                        MetadataInvalidKeyException.class,
                        () -> new Metadata().with("bad key", "v"));
        assertTrue(thrown.getMessage().contains("bad key"));
    }

    @Test
    void rejectsAnEmptyKey() {
        assertThrows(MetadataInvalidKeyException.class, () -> new Metadata().withAdded("", "v"));
    }

    @Test
    void normalizesAnUppercaseKeyRatherThanRejectingIt() {
        // Uppercase is lower-cased on the way in (like gRPC does), so it stays valid.
        MetadataContract metadata = new Metadata().with("Content-Type", "application/grpc");
        assertTrue(metadata.has("content-type"));
    }

    @Test
    void operationsAreImmutable() {
        MetadataContract base = new Metadata().with("k", "a");
        base.with("k", "b");
        base.withAdded("k", "c");
        base.without("k");
        assertEquals(List.of("a"), base.getAll("k"));
    }

    @Test
    void toMapReflectsContentsAndIsUnmodifiable() {
        MetadataContract metadata =
                new Metadata().withAdded("A", "1").withAdded("a", "2").with("b", "3");
        Map<String, List<Object>> map = metadata.toMap();
        assertEquals(List.of("1", "2"), map.get("a"));
        assertEquals(List.of("3"), map.get("b"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("c", List.of()));
    }

    @Test
    void getAllReturnedListIsUnmodifiable() {
        MetadataContract metadata = new Metadata().with("k", "a");
        assertThrows(UnsupportedOperationException.class, () -> metadata.getAll("k").add("b"));
    }

    @Test
    void iterationYieldsEntries() {
        MetadataContract metadata = new Metadata().with("a", "1").with("b", "2");
        int count = 0;
        for (Map.Entry<String, List<Object>> entry : metadata) {
            count += entry.getValue().size();
        }
        assertEquals(2, count);
    }

    @Test
    void keyWithEmptyValueListReturnsNullFirstValue() {
        Map<String, List<Object>> source = new java.util.HashMap<>();
        source.put("k", new java.util.ArrayList<>());
        Metadata metadata = new Metadata(source);
        assertTrue(metadata.has("k"));
        assertNull(metadata.get("k"));
    }

    @Test
    void constructorCopiesInput() {
        Map<String, List<Object>> source = new java.util.HashMap<>();
        List<Object> values = new java.util.ArrayList<>();
        values.add("a");
        source.put("K", values);

        Metadata metadata = new Metadata(source);
        values.add("b");
        source.remove("K");

        assertEquals(List.of("a"), metadata.getAll("k"));
    }
}
