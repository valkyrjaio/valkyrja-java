/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.routing.constant.Regex;
import org.junit.jupiter.api.Test;

/** Test the {@link Regex} constants holder. */
final class RegexTest {

    @Test
    void exposesRegexConstants() {
        assertEquals("\\d+", Regex.NUM);
        assertEquals("\\d+", Regex.ID);
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new Regex() {});
    }
}
