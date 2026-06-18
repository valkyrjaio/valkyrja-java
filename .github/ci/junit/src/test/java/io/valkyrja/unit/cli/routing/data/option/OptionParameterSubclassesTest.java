/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.data.option;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.routing.constant.OptionName;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.data.option.HelpOptionParameter;
import io.valkyrja.cli.routing.data.option.NoInteractionOptionParameter;
import io.valkyrja.cli.routing.data.option.QuietOptionParameter;
import io.valkyrja.cli.routing.data.option.SilentOptionParameter;
import io.valkyrja.cli.routing.data.option.VersionOptionParameter;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import org.junit.jupiter.api.Test;

/** Test the built-in {@link OptionParameter} subclasses. */
final class OptionParameterSubclassesTest {

    private static void assertValuelessOption(OptionParameter option, String expectedName) {
        assertSame(OptionValueMode.NONE, option.getValueMode());
        assertFalse(option.getShortNames().isEmpty());
        assertTrue(option.getName().equals(expectedName));
    }

    @Test
    void helpOption() {
        assertValuelessOption(new HelpOptionParameter(), OptionName.HELP);
    }

    @Test
    void noInteractionOption() {
        assertValuelessOption(new NoInteractionOptionParameter(), OptionName.NO_INTERACTION);
    }

    @Test
    void quietOption() {
        assertValuelessOption(new QuietOptionParameter(), OptionName.QUIET);
    }

    @Test
    void silentOption() {
        assertValuelessOption(new SilentOptionParameter(), OptionName.SILENT);
    }

    @Test
    void versionOption() {
        assertValuelessOption(new VersionOptionParameter(), OptionName.VERSION);
    }
}