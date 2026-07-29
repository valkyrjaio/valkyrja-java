/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.input.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.input.factory.InputFactory;
import org.junit.jupiter.api.Test;

/** Test the {@link InputFactory}. */
final class InputFactoryTest {

    @Test
    void fromGlobalsWithNoArgsUsesDefaults() {
        var input = InputFactory.fromGlobals(new String[] {}, "app", "list");

        assertEquals("app", input.getCaller());
        assertEquals("list", input.getCommandName());
        assertTrue(input.getArguments().isEmpty());
        assertTrue(input.getOptions().isEmpty());
    }

    @Test
    void fromGlobalsParsesCommandOptionsAndArguments() {
        var input =
                InputFactory.fromGlobals(
                        new String[] {"deploy", "-v", "--env=prod", "target"}, "app", "list");

        assertEquals("app", input.getCaller());
        assertEquals("deploy", input.getCommandName());
        assertEquals(1, input.getArguments().size());
        assertEquals("target", input.getArguments().get(0).getValue());
        assertEquals(2, input.getOptions().size());
        assertTrue(input.hasOption("v"));
        assertTrue(input.hasOption("env"));
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new InputFactory() {});
    }
}
