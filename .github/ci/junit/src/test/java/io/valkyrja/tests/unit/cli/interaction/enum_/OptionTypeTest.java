/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.enum_;

import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.enum_.OptionType;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionType} enum. */
final class OptionTypeTest {

    @Test
    void valueOfResolvesEachConstant() {
        for (OptionType type : OptionType.values()) {
            assertSame(type, OptionType.valueOf(type.name()));
        }
    }
}
