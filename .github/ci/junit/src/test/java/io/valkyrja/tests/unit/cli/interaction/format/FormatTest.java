/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.cli.interaction.format.Format;
import org.junit.jupiter.api.Test;

/** Test the {@link Format} value object. */
final class FormatTest {

    @Test
    void getters() {
        var format = new Format("1", "22");

        assertEquals("1", format.getSetCode());
        assertEquals("22", format.getUnsetCode());
    }

    @Test
    void withMethodsReturnImmutableCopies() {
        var original = new Format("1", "22");

        var withSet = original.withSetCode("4");
        var withUnset = original.withUnsetCode("24");

        assertNotSame(original, withSet);
        assertEquals("4", withSet.getSetCode());
        assertEquals("24", withUnset.getUnsetCode());
        // original unchanged
        assertEquals("1", original.getSetCode());
        assertEquals("22", original.getUnsetCode());
    }
}
