/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.stream.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.stream.enum_.ModeTranslation;
import org.junit.jupiter.api.Test;

/** Test the {@link ModeTranslation} enum. */
final class ModeTranslationTest {

    @Test
    void getValue() {
        assertEquals("", ModeTranslation.NONE.getValue());
        assertEquals("t", ModeTranslation.WINDOWS.getValue());
        assertEquals("b", ModeTranslation.BINARY_SAFE.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (ModeTranslation t : ModeTranslation.values()) {
            assertSame(t, ModeTranslation.valueOf(t.name()));
        }
    }
}
