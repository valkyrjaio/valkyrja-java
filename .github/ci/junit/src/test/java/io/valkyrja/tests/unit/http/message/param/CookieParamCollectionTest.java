/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.param;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.CookieParamCollection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link CookieParamCollection}. */
final class CookieParamCollectionTest {

    @Test
    void getCoercesValuesToString() {
        var raw = new HashMap<String, Object>();
        raw.put("name", "John");
        raw.put("count", 7);
        raw.put("nothing", null);
        var collection = new CookieParamCollection(raw);

        assertEquals("John", collection.get("name"));
        assertEquals("7", collection.get("count"));
        assertEquals("", collection.get("nothing"));
        assertEquals("", collection.get("missing"));
    }

    @Test
    void withCopiesAndFromArrayBuilds() {
        var collection = new CookieParamCollection(Map.of("a", "1"));

        assertTrue(collection.with(Map.of("x", "y")).has("x"));
        assertTrue(collection.withAdded(Map.of("z", "w")).has("a"));
        assertTrue(CookieParamCollection.fromArray(Map.of("q", "x")).has("q"));
    }
}
