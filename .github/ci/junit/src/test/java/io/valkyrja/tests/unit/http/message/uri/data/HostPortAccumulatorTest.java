/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.uri.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.http.message.uri.data.HostPortAccumulator;
import org.junit.jupiter.api.Test;

/** Test the {@link HostPortAccumulator} data holder. */
final class HostPortAccumulatorTest {

    @Test
    void defaultsAreEmpty() {
        var accumulator = new HostPortAccumulator();

        assertEquals("", accumulator.host);
        assertEquals(0, accumulator.port);
    }

    @Test
    void fieldsAreMutable() {
        var accumulator = new HostPortAccumulator();

        accumulator.host = "example.com";
        accumulator.port = 8080;

        assertEquals("example.com", accumulator.host);
        assertEquals(8080, accumulator.port);
    }
}
