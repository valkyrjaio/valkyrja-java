/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.enum_;

import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import org.junit.jupiter.api.Test;

/** Test the cli routing enums. */
final class CliRoutingEnumTest {

    @Test
    void argumentMode() {
        for (var v : ArgumentMode.values()) {
            assertSame(v, ArgumentMode.valueOf(v.name()));
        }
    }

    @Test
    void argumentValueMode() {
        for (var v : ArgumentValueMode.values()) {
            assertSame(v, ArgumentValueMode.valueOf(v.name()));
        }
    }

    @Test
    void optionMode() {
        for (var v : OptionMode.values()) {
            assertSame(v, OptionMode.valueOf(v.name()));
        }
    }

    @Test
    void optionValueMode() {
        for (var v : OptionValueMode.values()) {
            assertSame(v, OptionValueMode.valueOf(v.name()));
        }
    }
}
