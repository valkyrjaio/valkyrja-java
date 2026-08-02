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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.ServerParamCollection;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ServerParamCollection}. */
final class ServerParamCollectionTest {

    @Test
    void getReturnsValueOrEmptyStringDefault() {
        var collection = new ServerParamCollection(Map.of("page", "1"));

        assertEquals("1", collection.get("page"));
        assertEquals("", collection.get("missing"));
    }

    @Test
    void withCopiesAndFromArrayBuilds() {
        var collection = new ServerParamCollection(Map.of("page", "1"));

        assertTrue(collection.with(Map.of("a", "b")).has("a"));
        assertFalse(collection.with(Map.of("a", "b")).has("page"));
        assertTrue(collection.withAdded(Map.of("c", "d")).has("page"));
        assertTrue(ServerParamCollection.fromArray(Map.of("q", "x")).has("q"));
    }
}
