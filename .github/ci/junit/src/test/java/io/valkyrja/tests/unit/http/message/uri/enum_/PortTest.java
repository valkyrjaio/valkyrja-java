/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.uri.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.uri.enum_.Port;
import org.junit.jupiter.api.Test;

/** Test the uri {@link Port} enum. */
final class PortTest {

    @Test
    void values() {
        assertEquals(80, Port.HTTP.getValue());
        assertEquals(433, Port.HTTPS.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (Port port : Port.values()) {
            assertSame(port, Port.valueOf(port.name()));
        }
    }
}
