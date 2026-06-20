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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.QueryParamCollection;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link QueryParamCollection}. */
final class QueryParamCollectionTest {

    @Test
    void getReturnsValueOrEmptyStringDefault() {
        var collection = new QueryParamCollection(Map.of("page", "1"));

        assertEquals("1", collection.get("page"));
        assertEquals("", collection.get("missing"));
    }

    @Test
    void withCopiesAndFromArrayBuilds() {
        var collection = new QueryParamCollection(Map.of("page", "1"));

        assertTrue(collection.with(Map.of("a", "b")).has("a"));
        assertFalse(collection.with(Map.of("a", "b")).has("page"));
        assertTrue(collection.withAdded(Map.of("c", "d")).has("page"));
        assertTrue(QueryParamCollection.fromArray(Map.of("q", "x")).has("q"));
    }
}
