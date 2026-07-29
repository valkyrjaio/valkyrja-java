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

import io.valkyrja.cli.interaction.enum_.Style;
import org.junit.jupiter.api.Test;

/** Test the {@link Style} enum, including the per-style reset code. */
final class StyleTest {

    @Test
    void valueAndPerStyleDefault() {
        assertEquals(1, Style.BOLD.value);
        assertEquals(22, Style.BOLD.getDefault());
        assertEquals(24, Style.UNDERSCORE.getDefault());
        assertEquals(25, Style.BLINK.getDefault());
        assertEquals(27, Style.INVERSE.getDefault());
        assertEquals(28, Style.CONCEAL.getDefault());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (Style style : Style.values()) {
            assertSame(style, Style.valueOf(style.name()));
        }
    }
}
