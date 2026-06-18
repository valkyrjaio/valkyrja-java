/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.argument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.cli.interaction.argument.Argument;
import org.junit.jupiter.api.Test;

/** Test the {@link Argument} value object. */
final class ArgumentTest {

    @Test
    void getValue() {
        assertEquals("value", new Argument("value").getValue());
    }

    @Test
    void withValueReturnsImmutableCopy() {
        var original = new Argument("value");

        var copy = original.withValue("other");

        assertNotSame(original, copy);
        assertEquals("other", copy.getValue());
        assertEquals("value", original.getValue());
    }
}