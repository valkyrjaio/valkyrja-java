/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;
import io.valkyrja.cli.interaction.format.BackgroundColorFormat;
import org.junit.jupiter.api.Test;

/** Test the {@link BackgroundColorFormat}. */
final class BackgroundColorFormatTest {

    @Test
    void usesColorCodeAndDefaultReset() {
        var format = new BackgroundColorFormat(BackgroundColor.RED);

        assertEquals("41", format.getSetCode());
        assertEquals("49", format.getUnsetCode());
    }
}
