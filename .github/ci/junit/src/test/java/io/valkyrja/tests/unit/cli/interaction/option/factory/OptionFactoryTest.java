/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.option.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.factory.OptionFactory;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidNonEmptyValueException;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionInvalidOptionNameException;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionFactory}. */
final class OptionFactoryTest {

    @Test
    void parsesLongOptionWithValue() {
        var options = OptionFactory.fromArg("--name=value");

        assertEquals(1, options.size());
        assertEquals("name", options.get(0).getName());
        assertEquals("value", options.get(0).getValue());
        assertSame(OptionType.LONG, options.get(0).getType());
    }

    @Test
    void parsesLongFlagWithoutValue() {
        var options = OptionFactory.fromArg("--flag");

        assertEquals(1, options.size());
        assertEquals("flag", options.get(0).getName());
        assertEquals("", options.get(0).getValue());
    }

    @Test
    void parsesSingleShortOption() {
        var options = OptionFactory.fromArg("-v");

        assertEquals(1, options.size());
        assertEquals("v", options.get(0).getName());
        assertSame(OptionType.SHORT, options.get(0).getType());
    }

    @Test
    void splitsCombinedShortOptions() {
        var options = OptionFactory.fromArg("-abc");

        assertEquals(3, options.size());
        assertEquals("a", options.get(0).getName());
        assertEquals("b", options.get(1).getName());
        assertEquals("c", options.get(2).getName());
    }

    @Test
    void rejectsArgumentNotStartingWithDash() {
        assertThrows(
                CliInteractionInvalidOptionNameException.class,
                () -> OptionFactory.fromArg("name"));
    }

    @Test
    void rejectsEmptyOptionName() {
        assertThrows(
                CliInteractionInvalidNonEmptyValueException.class,
                () -> OptionFactory.fromArg("--=value"));
    }

    @Test
    void rejectsCombinedShortOptionsWithValue() {
        assertThrows(
                CliInteractionInvalidEmptyValueException.class,
                () -> OptionFactory.fromArg("-ab=value"));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new OptionFactory() {});
    }
}
