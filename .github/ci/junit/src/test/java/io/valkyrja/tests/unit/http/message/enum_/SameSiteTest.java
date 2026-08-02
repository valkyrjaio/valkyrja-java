/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.enum_.SameSite;
import org.junit.jupiter.api.Test;

/** Test the {@link SameSite} enum. */
final class SameSiteTest {

    @Test
    void getValue() {
        assertEquals("lax", SameSite.LAX.getValue());
        assertEquals("strict", SameSite.STRICT.getValue());
        assertEquals("none", SameSite.NONE.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (SameSite s : SameSite.values()) {
            assertSame(s, SameSite.valueOf(s.name()));
        }
    }
}
