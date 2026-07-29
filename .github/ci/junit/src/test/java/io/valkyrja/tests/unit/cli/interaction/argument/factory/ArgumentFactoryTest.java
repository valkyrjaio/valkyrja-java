/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.argument.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.interaction.argument.factory.ArgumentFactory;
import org.junit.jupiter.api.Test;

/** Test the {@link ArgumentFactory}. */
final class ArgumentFactoryTest {

    @Test
    void fromArgCreatesArgument() {
        assertEquals("value", ArgumentFactory.fromArg("value").getValue());
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new ArgumentFactory() {});
    }
}
