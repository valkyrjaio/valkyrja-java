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

import io.valkyrja.cli.interaction.message.WarningMessage;
import org.junit.jupiter.api.Test;

/** Test the {@link WarningMessage}. */
final class WarningMessageTest {

    @Test
    void appliesWarningFormatter() {
        assertEquals("\033[30;43mwarn\033[39;49m", new WarningMessage("warn").getFormattedText());
    }
}
