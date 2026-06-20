/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.param;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ParsedJsonParamCollection}. */
final class ParsedJsonParamCollectionTest {

    @Test
    void getReturnsValueOrNullDefault() {
        var collection = new ParsedJsonParamCollection(Map.of("a", "1"));

        assertEquals("1", collection.get("a"));
        assertNull(collection.get("missing"));
    }

    @Test
    void withCopiesAndFromArrayBuilds() {
        var collection = new ParsedJsonParamCollection(Map.of("a", "1"));

        assertTrue(collection.with(Map.of("x", "y")).has("x"));
        assertTrue(collection.withAdded(Map.of("z", "w")).has("a"));
        assertTrue(ParsedJsonParamCollection.fromArray(Map.of("q", "x")).has("q"));
    }
}
