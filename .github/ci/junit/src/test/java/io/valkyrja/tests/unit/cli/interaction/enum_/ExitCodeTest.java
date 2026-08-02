/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.enum_.ExitCode;
import org.junit.jupiter.api.Test;

/** Test the {@link ExitCode} enum. */
final class ExitCodeTest {

    @Test
    void wellKnownCodes() {
        assertEquals(0, ExitCode.SUCCESS.value);
        assertEquals(1, ExitCode.ERROR.value);
        assertEquals(255, ExitCode.AUTO_EXIT.value);
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (ExitCode code : ExitCode.values()) {
            assertSame(code, ExitCode.valueOf(code.name()));
        }
    }
}
