/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.header.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetSetException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetUnsetException;
import io.valkyrja.http.message.header.value.Value;
import io.valkyrja.http.message.header.value.component.Component;
import org.junit.jupiter.api.Test;

/** Test the {@link Value} header value. */
final class ValueTest {

    @Test
    void fromValueSplitsOnSemicolon() {
        var value = Value.fromValue("a; b; c");

        assertEquals(3, value.getComponents().size());
    }

    @Test
    void fromValueSingleComponent() {
        var value = Value.fromValue("only");

        assertEquals(1, value.getComponents().size());
    }

    @Test
    void toStringJoinsComponents() {
        var value = new Value("a", new Component("k", "v"));

        assertEquals("a; k=v", value.toString());
        assertEquals("a; k=v", value.jsonSerialize());
    }

    @Test
    void withComponentsReplacesAndWithAddedAppends() {
        var original = new Value("a");

        assertEquals(1, original.withComponents("x").getComponents().size());
        assertEquals(2, ((Value) original.withAddedComponents("y")).getComponents().size());
        // original is unchanged
        assertEquals(1, original.getComponents().size());
    }

    @Test
    void iterationProtocol() {
        var value = new Value("a", "b");

        assertEquals(2, value.count());
        assertTrue(value.offsetExists(0));
        assertFalse(value.offsetExists(5));
        assertEquals("a", value.offsetGet(0));
        assertTrue(value.valid());
        assertEquals(0, value.key());
        assertEquals("a", value.current());
        value.next();
        assertEquals(1, value.key());
        value.rewind();
        assertEquals(0, value.key());
    }

    @Test
    void offsetMutationsAreUnsupported() {
        var value = new Value("a");

        assertThrows(
                HttpHeaderUnsupportedOffsetSetException.class, () -> value.offsetSet(0, "x"));
        assertThrows(
                HttpHeaderUnsupportedOffsetUnsetException.class, () -> value.offsetUnset(0));
    }

    @Test
    void iteratorYieldsOnlyComponents() {
        var value = new Value("plain", new Component("k", "v"));

        int count = 0;
        for (var component : value) {
            assertEquals("k=v", component.toString());
            count++;
        }
        assertEquals(1, count);
    }
}
