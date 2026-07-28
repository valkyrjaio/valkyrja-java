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

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.Messages;
import org.junit.jupiter.api.Test;

/** Test the {@link Messages}. */
final class MessagesTest {

    @Test
    void concatenatesTextAndFormattedText() {
        var messages = new Messages(new Message("a"), new Message("b"), new Message("c"));

        assertEquals("abc", messages.getText());
        assertEquals("abc", messages.getFormattedText());
    }
}
