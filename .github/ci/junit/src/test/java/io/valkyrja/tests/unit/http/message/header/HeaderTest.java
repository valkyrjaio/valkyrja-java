/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidNameException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetSetException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetUnsetException;
import io.valkyrja.http.message.header.value.Value;
import org.junit.jupiter.api.Test;

/** Test the {@link Header}. */
final class HeaderTest {

    @Test
    void constructorNormalizesName() {
        var header = new Header("Content-Type", "text/html");

        assertEquals("Content-Type", header.getName());
        assertEquals("content-type", header.getNormalizedName());
    }

    @Test
    void fromValueSplitsNameAndValues() {
        var header = (Header) Header.fromValue("Accept: text/html, application/json");

        assertEquals("Accept", header.getName());
        assertEquals(2, header.getValues().size());
    }

    @Test
    void fromValueWithoutColonHasNoValues() {
        var header = (Header) Header.fromValue("Standalone");

        assertEquals("Standalone", header.getName());
        assertTrue(header.getValues().isEmpty());
    }

    @Test
    void withMethods() {
        var header = new Header("X-Test", "a");

        assertEquals("X-Other", header.withName("X-Other").getName());
        assertEquals(1, header.withValues("b").getValues().size());
        assertEquals(2, ((Header) header.withAddedValues("c")).getValues().size());
        assertEquals(1, header.getValues().size());
    }

    @Test
    void headerLineAndToString() {
        var header = new Header("X-Test", "a", "b");

        assertEquals("a, b", header.getHeaderLine());
        assertEquals("X-Test: a, b", header.toString());
        assertEquals("X-Test: a, b", header.jsonSerialize());
    }

    @Test
    void toStringEmptyWhenNoValues() {
        assertEquals("", new Header("X-Empty").toString());
    }

    @Test
    void valuesAcceptValueContracts() {
        var header = new Header("X-Test", new Value("a", "b"));

        assertEquals("X-Test: a; b", header.toString());
    }

    @Test
    void invalidNameThrows() {
        assertThrows(HttpHeaderInvalidNameException.class, () -> new Header("bad name"));
    }

    @Test
    void iterationProtocol() {
        var header = new Header("X-Test", "a", "b");

        assertEquals(2, header.count());
        assertTrue(header.offsetExists(0));
        assertFalse(header.offsetExists(9));
        assertEquals("a", header.offsetGet(0));
        assertTrue(header.valid());
        assertEquals(0, header.key());
        assertEquals("a", header.current());
        header.next();
        assertEquals(1, header.key());
        header.rewind();
        assertEquals(0, header.key());

        int count = 0;
        for (var ignored : header) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void offsetMutationsUnsupported() {
        var header = new Header("X-Test", "a");

        assertThrows(HttpHeaderUnsupportedOffsetSetException.class, () -> header.offsetSet(0, "x"));
        assertThrows(HttpHeaderUnsupportedOffsetUnsetException.class, () -> header.offsetUnset(0));
    }

    @Test
    void offsetExistsRejectsNegativeOffset() {
        assertFalse(new Header("x-test", "a").offsetExists(-1));
    }

    @Test
    void headerLineSkipsEmptyValues() {
        assertEquals("a", new Header("x-test", "a", "").getHeaderLine());
    }

    @Test
    void iteratorValidityFlipsAtEnd() {
        var header = new Header("x-test", "a", "b");
        header.rewind();
        int count = 0;
        while (header.valid()) {
            header.next();
            count++;
        }

        assertEquals(2, count);
    }
}
