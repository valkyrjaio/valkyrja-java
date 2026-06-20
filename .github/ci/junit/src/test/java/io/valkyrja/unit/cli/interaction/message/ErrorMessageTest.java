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
import io.valkyrja.cli.interaction.message.ErrorMessage;
import org.junit.jupiter.api.Test;

/** Test the {@link ErrorMessage}. */
final class ErrorMessageTest {

    @Test
    void appliesErrorFormatter() {
        var message = new ErrorMessage("err");

        assertEquals("err", message.getText());
        assertEquals("\033[97;41merr\033[39;49m", message.getFormattedText());
    }
}
