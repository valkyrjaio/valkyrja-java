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

import io.valkyrja.cli.interaction.message.SuccessMessage;
import org.junit.jupiter.api.Test;

/** Test the {@link SuccessMessage}. */
final class SuccessMessageTest {

    @Test
    void appliesSuccessFormatter() {
        assertEquals("\033[97;42mok\033[39;49m", new SuccessMessage("ok").getFormattedText());
    }
}
