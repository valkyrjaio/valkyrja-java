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

import io.valkyrja.http.message.uri.enum_.Scheme;
import org.junit.jupiter.api.Test;

/** Test the {@link Scheme}. */
final class SchemeTest {

    @Test
    void exposesValuesAndStringForm() {
        assertEquals(3, Scheme.values().length);
        assertSame(Scheme.HTTPS, Scheme.valueOf("HTTPS"));
        assertEquals("", Scheme.EMPTY.getValue());
        assertEquals("http", Scheme.HTTP.getValue());
        assertEquals("https", Scheme.HTTPS.getValue());
    }
}
