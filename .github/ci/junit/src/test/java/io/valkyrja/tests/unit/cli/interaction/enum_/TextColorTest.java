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

import io.valkyrja.cli.interaction.enum_.TextColor;
import org.junit.jupiter.api.Test;

/** Test the {@link TextColor} enum. */
final class TextColorTest {

    @Test
    void valueAndDefault() {
        assertEquals(31, TextColor.RED.value);
        assertEquals(39, TextColor.RED.getDefault());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (TextColor color : TextColor.values()) {
            assertSame(color, TextColor.valueOf(color.name()));
        }
    }
}
