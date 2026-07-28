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

import io.valkyrja.cli.interaction.formatter.WarningFormatter;
import org.junit.jupiter.api.Test;

/** Test the {@link WarningFormatter}. */
final class WarningFormatterTest {

    @Test
    void usesBlackOnYellow() {
        assertEquals("\033[30;43mx\033[39;49m", new WarningFormatter().formatText("x"));
    }
}
