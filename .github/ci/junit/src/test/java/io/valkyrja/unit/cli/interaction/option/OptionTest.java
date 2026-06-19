/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.Option;
import org.junit.jupiter.api.Test;

/** Test the {@link Option} value object. */
final class OptionTest {

    @Test
    void fullConstructorExposesNameValueAndType() {
        var option = new Option("name", "value", OptionType.LONG);

        assertEquals("name", option.getName());
        assertEquals("value", option.getValue());
        assertSame(OptionType.LONG, option.getType());
        assertTrue(option.hasValue());
    }

    @Test
    void nameAndTypeConstructorDefaultsToEmptyValue() {
        var option = new Option("name", OptionType.SHORT);

        assertEquals("", option.getValue());
        assertFalse(option.hasValue());
    }

    @Test
    void withMethodsReturnImmutableCopies() {
        var original = new Option("name", "value", OptionType.LONG);

        assertNotSame(original, original.withName("other"));
        assertEquals("other", original.withName("other").getName());
        assertEquals("changed", original.withValue("changed").getValue());
        assertFalse(original.withoutValue().hasValue());
        assertSame(OptionType.SHORT, original.withType(OptionType.SHORT).getType());
        // original unchanged
        assertEquals("name", original.getName());
        assertEquals("value", original.getValue());
        assertSame(OptionType.LONG, original.getType());
    }
}
