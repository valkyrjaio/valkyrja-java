/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
