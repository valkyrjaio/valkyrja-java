/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.uri.type;

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
