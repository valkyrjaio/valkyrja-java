/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.header.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidHeaderNameException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidHeaderParamException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link HeaderCollection}. */
final class HeaderCollectionTest {

    private HeaderCollection collection() {
        return new HeaderCollection(
                new Header("Content-Type", "text/html"), new Header("Accept", "application/json"));
    }

    @Test
    void hasAndGet() {
        var collection = collection();

        assertTrue(collection.has("content-type"));
        assertTrue(collection.has("CONTENT-TYPE"));
        assertFalse(collection.has("missing"));
        assertEquals("Content-Type", collection.get("content-type").getName());
    }

    @Test
    void getThrowsForMissing() {
        assertThrows(
                HttpHeaderInvalidHeaderNameException.class, () -> collection().get("missing"));
    }

    @Test
    void getHeaderLine() {
        assertEquals("text/html", collection().getHeaderLine("content-type"));
        assertEquals("", collection().getHeaderLine("missing"));
    }

    @Test
    void getAllOnlyAndExcept() {
        var collection = collection();

        assertEquals(2, collection.getAll().size());
        assertEquals(1, collection.getOnly("content-type").size());
        assertEquals(1, collection.getAllExcept("content-type").size());
    }

    @Test
    void withHeaderAndWithoutHeader() {
        var collection = collection();

        var added = collection.withHeader(new Header("X-Extra", "v"));
        assertTrue(added.has("x-extra"));

        assertFalse(collection.withoutHeader("content-type").has("content-type"));
        // Removing a missing header is a no-op copy.
        assertTrue(collection.withoutHeader("missing").has("content-type"));
    }

    @Test
    void withHeadersReplacesAndWithoutHeadersRemoves() {
        var collection = collection();

        assertEquals(1, ((HeaderCollection) collection.withHeaders(new Header("Only", "v"))).getAll().size());
        assertFalse(
                collection.withoutHeaders("content-type", "accept").has("content-type"));
        // Removing a header that is not present is a no-op.
        assertTrue(collection.withoutHeaders("missing").has("content-type"));
    }

    @Test
    void withAddedHeadersMergesExisting() {
        var collection = collection();

        var merged = collection.withAddedHeaders(new Header("Content-Type", "text/plain"));

        assertEquals(2, merged.get("content-type").getValues().size());
    }

    @Test
    void withAddedHeadersAppendsNew() {
        var merged = collection().withAddedHeaders(new Header("X-New", "v"));

        assertTrue(merged.has("x-new"));
    }

    @Test
    void fromArrayBuildsCollection() {
        Map<String, HeaderContract> data = new LinkedHashMap<>();
        data.put("content-type", new Header("Content-Type", "text/html"));

        assertTrue(HeaderCollection.fromArray(data).has("content-type"));
    }

    @Test
    void fromArrayRejectsNonHeaderValues() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("bad", "not a header");

        assertThrows(
                HttpHeaderInvalidHeaderParamException.class, () -> HeaderCollection.fromArray(data));
    }
}
