/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.param;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.param.abstract_.ParamCollection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the abstract {@link ParamCollection} base (via {@link ServerParamCollection}). */
final class ParamCollectionTest {

    private static ServerParamCollection collection() {
        return new ServerParamCollection(Map.of("a", "1", "b", "2", "c", "3"));
    }

    @Test
    void acceptsScalarAndNullParams() {
        var params = new java.util.LinkedHashMap<String, Object>();
        params.put("s", "x");
        params.put("i", 1);
        params.put("l", 1L);
        params.put("d", 1.0);
        params.put("f", 1.0f);
        params.put("bool", true);
        params.put("nil", null);

        assertEquals(7, new ServerParamCollection(params).getAll().size());
    }

    @Test
    void rejectsNonScalarParams() {
        var params = Map.<String, Object>of("bad", List.of(1, 2));

        assertThrows(IllegalArgumentException.class, () -> new ServerParamCollection(params));
    }

    @Test
    void hasGetAndGetAll() {
        var collection = collection();

        assertTrue(collection.has("a"));
        assertFalse(collection.has("z"));
        assertEquals("1", collection.get("a"));
        assertEquals(3, collection.getAll().size());
    }

    @Test
    void getOnlyAndGetAllExcept() {
        var collection = collection();

        assertEquals(1, collection.getOnly("a").size());
        assertEquals(2, collection.getAllExcept("a").size());
    }

    @Test
    void withReplacesAndWithAddedMerges() {
        var collection = collection();

        assertEquals(1, collection.with(Map.of("x", "9")).getAll().size());
        assertEquals(4, collection.withAdded(Map.of("d", "4")).getAll().size());
        // original unchanged
        assertEquals(3, collection.getAll().size());
    }

    @Test
    void baseFromArrayIsUnsupported() {
        assertThrows(
                UnsupportedOperationException.class, () -> ParamCollection.fromArray(Map.of()));
    }

    @Test
    void baseGetReturnsRawValueOrNull() {
        var collection =
                new ParamCollection(Map.of("k", "v")) {
                    @Override
                    protected ParamCollection copy() {
                        return this;
                    }
                };

        assertEquals("v", collection.get("k"));
        assertNull(collection.get("missing"));
    }
}
