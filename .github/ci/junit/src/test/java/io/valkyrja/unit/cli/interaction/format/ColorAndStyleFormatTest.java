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
import io.valkyrja.cli.interaction.enum_.Style;
import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.BackgroundColorFormat;
import io.valkyrja.cli.interaction.format.StyleFormat;
import io.valkyrja.cli.interaction.format.TextColorFormat;
import org.junit.jupiter.api.Test;

/** Test the color/style {@link io.valkyrja.cli.interaction.format.Format} subclasses. */
final class ColorAndStyleFormatTest {

    @Test
    void textColorFormatUsesColorCodeAndDefaultReset() {
        var format = new TextColorFormat(TextColor.RED);

        assertEquals("31", format.getSetCode());
        assertEquals("39", format.getUnsetCode());
    }

    @Test
    void backgroundColorFormatUsesColorCodeAndDefaultReset() {
        var format = new BackgroundColorFormat(BackgroundColor.RED);

        assertEquals("41", format.getSetCode());
        assertEquals("49", format.getUnsetCode());
    }

    @Test
    void styleFormatUsesStyleCodeAndPerStyleReset() {
        // Exercise every Style so the getDefault() switch is fully covered.
        assertEquals("1", new StyleFormat(Style.BOLD).getSetCode());
        assertEquals("22", new StyleFormat(Style.BOLD).getUnsetCode());
        assertEquals("24", new StyleFormat(Style.UNDERSCORE).getUnsetCode());
        assertEquals("25", new StyleFormat(Style.BLINK).getUnsetCode());
        assertEquals("27", new StyleFormat(Style.INVERSE).getUnsetCode());
        assertEquals("28", new StyleFormat(Style.CONCEAL).getUnsetCode());
    }
}
