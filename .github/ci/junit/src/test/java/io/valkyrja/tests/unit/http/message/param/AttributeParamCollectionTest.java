/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.param;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.AttributeParamCollection;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link AttributeParamCollection}. */
final class AttributeParamCollectionTest {

    @Test
    void noArgConstructorIsEmpty() {
        assertTrue(new AttributeParamCollection().getAll().isEmpty());
    }

    @Test
    void getReturnsRawScalarOrNullDefault() {
        var collection =
                new AttributeParamCollection(Map.of("name", "John", "age", 30, "active", true));

        assertEquals("John", collection.get("name"));
        assertEquals(30, collection.get("age"));
        assertEquals(true, collection.get("active"));
        assertNull(collection.get("missing"));
    }

    @Test
    void withCopiesAndFromArrayBuilds() {
        var collection = new AttributeParamCollection(Map.of("a", "1"));

        assertTrue(collection.with(Map.of("x", "y")).has("x"));
        assertTrue(collection.withAdded(Map.of("z", "w")).has("a"));
        assertTrue(AttributeParamCollection.fromArray(Map.of("q", "x")).has("q"));
    }
}
