/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.cli.interaction.argument.Argument;
import org.junit.jupiter.api.Test;

/** Test the {@link Argument} value object. */
final class ArgumentTest {

    @Test
    void getValue() {
        assertEquals("value", new Argument("value").getValue());
    }

    @Test
    void withValueReturnsImmutableCopy() {
        var original = new Argument("value");

        var copy = original.withValue("other");

        assertNotSame(original, copy);
        assertEquals("other", copy.getValue());
        assertEquals("value", original.getValue());
    }
}
