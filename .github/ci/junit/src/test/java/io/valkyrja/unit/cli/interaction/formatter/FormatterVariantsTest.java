/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.cli.interaction.formatter.ErrorFormatter;
import io.valkyrja.cli.interaction.formatter.HighlightedTextFormatter;
import io.valkyrja.cli.interaction.formatter.QuestionFormatter;
import io.valkyrja.cli.interaction.formatter.SuccessFormatter;
import io.valkyrja.cli.interaction.formatter.WarningFormatter;
import org.junit.jupiter.api.Test;

/** Test the preconfigured {@link io.valkyrja.cli.interaction.formatter.Formatter} subclasses. */
final class FormatterVariantsTest {

    @Test
    void errorFormatterUsesLightWhiteOnRed() {
        assertEquals(2, new ErrorFormatter().getFormats().size());
        assertEquals("\033[97;41mx\033[39;49m", new ErrorFormatter().formatText("x"));
    }

    @Test
    void successFormatterUsesLightWhiteOnGreen() {
        assertEquals("\033[97;42mx\033[39;49m", new SuccessFormatter().formatText("x"));
    }

    @Test
    void warningFormatterUsesBlackOnYellow() {
        assertEquals("\033[30;43mx\033[39;49m", new WarningFormatter().formatText("x"));
    }

    @Test
    void questionFormatterUsesMagentaText() {
        assertEquals("\033[35mx\033[39m", new QuestionFormatter().formatText("x"));
    }

    @Test
    void highlightedTextFormatterUsesYellowText() {
        assertEquals("\033[33mx\033[39m", new HighlightedTextFormatter().formatText("x"));
    }
}
