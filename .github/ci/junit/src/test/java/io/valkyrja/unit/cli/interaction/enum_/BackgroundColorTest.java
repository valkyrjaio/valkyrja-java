/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;
import org.junit.jupiter.api.Test;

/** Test the {@link BackgroundColor} enum. */
final class BackgroundColorTest {

    @Test
    void valueAndDefault() {
        assertEquals(41, BackgroundColor.RED.value);
        assertEquals(49, BackgroundColor.RED.getDefault());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (BackgroundColor color : BackgroundColor.values()) {
            assertSame(color, BackgroundColor.valueOf(color.name()));
        }
    }
}
