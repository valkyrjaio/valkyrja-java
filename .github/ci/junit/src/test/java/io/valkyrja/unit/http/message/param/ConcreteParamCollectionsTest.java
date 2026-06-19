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

import io.valkyrja.http.message.param.AttributeParamCollection;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the concrete param collection subclasses. */
final class ConcreteParamCollectionsTest {

    @Test
    void fromArrayBuildsEachCollection() {
        var data = Map.<String, Object>of("k", "v");

        assertTrue(ServerParamCollection.fromArray(data).has("k"));
        assertTrue(CookieParamCollection.fromArray(data).has("k"));
        assertTrue(QueryParamCollection.fromArray(data).has("k"));
        assertTrue(ParsedBodyParamCollection.fromArray(data).has("k"));
        assertTrue(ParsedJsonParamCollection.fromArray(data).has("k"));
        assertTrue(AttributeParamCollection.fromArray(data).has("k"));
    }

    @Test
    void getDefaultsToEmptyStringForServerCookieQueryAndParsedBody() {
        assertEquals("", new ServerParamCollection(Map.of()).get("missing"));
        assertEquals("", new CookieParamCollection(Map.of()).get("missing"));
        assertEquals("", new QueryParamCollection(Map.of()).get("missing"));
        assertEquals("", new ParsedBodyParamCollection(Map.of()).get("missing"));
        // Cookie coerces non-string values to their string form.
        assertEquals("7", new CookieParamCollection(Map.of("n", 7)).get("n"));
    }

    @Test
    void getDefaultsToNullForAttributeAndParsedJson() {
        assertNull(new AttributeParamCollection().get("missing"));
        assertNull(new AttributeParamCollection(Map.of()).get("missing"));
        assertNull(new ParsedJsonParamCollection(Map.of()).get("missing"));
    }

    @Test
    void withReturnsSameTypeForEachCollection() {
        assertTrue(new CookieParamCollection(Map.of()).with(Map.of("a", "b")).has("a"));
        assertTrue(new QueryParamCollection(Map.of()).withAdded(Map.of("a", "b")).has("a"));
        assertTrue(new ParsedJsonParamCollection(Map.of()).with(Map.of("a", "b")).has("a"));
        assertTrue(new ServerParamCollection(Map.of()).with(Map.of("a", "b")).has("a"));
        assertTrue(new ParsedBodyParamCollection(Map.of()).withAdded(Map.of("a", "b")).has("a"));
        assertTrue(new AttributeParamCollection(Map.of()).with(Map.of("a", "b")).has("a"));
    }
}