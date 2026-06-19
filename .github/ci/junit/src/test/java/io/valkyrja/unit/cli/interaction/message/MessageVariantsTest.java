/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Banner;
import io.valkyrja.cli.interaction.message.ErrorMessage;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.Messages;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.SuccessMessage;
import io.valkyrja.cli.interaction.message.WarningMessage;
import org.junit.jupiter.api.Test;

/** Test the {@link Message} subclasses. */
final class MessageVariantsTest {

    @Test
    void messagesConcatenatesTextAndFormattedText() {
        var messages = new Messages(new Message("a"), new Message("b"), new Message("c"));

        assertEquals("abc", messages.getText());
        assertEquals("abc", messages.getFormattedText());
    }

    @Test
    void newLineIsANewlineCharacter() {
        assertEquals("\n", new NewLine().getText());
        assertEquals("\n", new NewLine(null).getText());
    }

    @Test
    void bannerPadsTextWithBlankLines() {
        var banner = new Banner(new Message("Hi"));

        var text = banner.getText();
        assertTrue(text.contains("    Hi    "));
        assertTrue(text.startsWith("\n"));
        assertEquals(4, text.chars().filter(c -> c == '\n').count());
        // No formatter set on the inner messages — formatted text equals plain text.
        assertEquals(text, banner.getFormattedText());
    }

    @Test
    void errorMessageAppliesErrorFormatter() {
        var message = new ErrorMessage("err");

        assertEquals("err", message.getText());
        assertEquals("\033[97;41merr\033[39;49m", message.getFormattedText());
    }

    @Test
    void successMessageAppliesSuccessFormatter() {
        assertEquals("\033[97;42mok\033[39;49m", new SuccessMessage("ok").getFormattedText());
    }

    @Test
    void warningMessageAppliesWarningFormatter() {
        assertEquals("\033[30;43mwarn\033[39;49m", new WarningMessage("warn").getFormattedText());
    }
}
