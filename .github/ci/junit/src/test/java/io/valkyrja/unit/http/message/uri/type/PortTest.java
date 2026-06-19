/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.uri.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;
import io.valkyrja.http.message.uri.type.Port;
import org.junit.jupiter.api.Test;

/** Test the {@link Port} type. */
final class PortTest {

    @Test
    void validPort() {
        assertEquals(8080, new Port(8080).asFlatValue());
    }

    @Test
    void rejectsOutOfRangePorts() {
        assertThrows(HttpUriInvalidPortException.class, () -> new Port(0));
        assertThrows(HttpUriInvalidPortException.class, () -> new Port(70000));
    }

    @Test
    void fromValueAcceptsInteger() {
        assertEquals(443, Port.fromValue(443).asFlatValue());
    }

    @Test
    void fromValueRejectsNonInteger() {
        assertThrows(HttpUriInvalidPortException.class, () -> Port.fromValue("80"));
        assertThrows(HttpUriInvalidPortException.class, () -> Port.fromValue(null));
    }
}