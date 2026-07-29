/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Banner;
import io.valkyrja.cli.interaction.message.Message;
import org.junit.jupiter.api.Test;

/** Test the {@link Banner}. */
final class BannerTest {

    @Test
    void padsTextWithBlankLines() {
        var banner = new Banner(new Message("Hi"));

        var text = banner.getText();
        assertTrue(text.contains("    Hi    "));
        assertTrue(text.startsWith("\n"));
        assertEquals(4, text.chars().filter(c -> c == '\n').count());
        // No formatter set on the inner messages — formatted text equals plain text.
        assertEquals(text, banner.getFormattedText());
    }
}
