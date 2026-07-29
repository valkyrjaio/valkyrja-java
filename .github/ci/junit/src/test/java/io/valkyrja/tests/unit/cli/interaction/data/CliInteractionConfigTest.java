/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import org.junit.jupiter.api.Test;

/** Test the {@link CliInteractionConfig}. */
final class CliInteractionConfigTest {

    @Test
    void defaults() {
        var config = new CliInteractionConfig();

        assertFalse(config.isQuiet());
        assertTrue(config.isInteractive());
        assertFalse(config.isSilent());
    }

    @Test
    void fullConstructor() {
        var config = new CliInteractionConfig(true, false, true);

        assertTrue(config.isQuiet());
        assertFalse(config.isInteractive());
        assertTrue(config.isSilent());
    }

    @Test
    void setters() {
        var config = new CliInteractionConfig();

        config.setQuiet(true);
        config.setInteractive(false);
        config.setSilent(true);

        assertTrue(config.isQuiet());
        assertFalse(config.isInteractive());
        assertTrue(config.isSilent());
    }
}
