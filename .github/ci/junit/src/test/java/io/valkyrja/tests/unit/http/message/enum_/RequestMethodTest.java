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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.RequestMethod;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestMethod} enum. */
final class RequestMethodTest {

    @Test
    void getValue() {
        assertEquals("GET", RequestMethod.GET.getValue());
    }

    @Test
    void allExcludesAny() {
        assertEquals(9, RequestMethod.all().size());
        assertTrue(RequestMethod.all().contains(RequestMethod.GET));
    }

    @Test
    void fromIsCaseInsensitiveAndRejectsUnknown() {
        assertSame(RequestMethod.POST, RequestMethod.from("post"));
        assertThrows(IllegalArgumentException.class, () -> RequestMethod.from("FETCH"));
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (RequestMethod m : RequestMethod.values()) {
            assertSame(m, RequestMethod.valueOf(m.name()));
        }
    }
}
