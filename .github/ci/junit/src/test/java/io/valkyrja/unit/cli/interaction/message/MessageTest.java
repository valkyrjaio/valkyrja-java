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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.formatter.Formatter;
import io.valkyrja.cli.interaction.format.Format;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.throwable.exception.CliInteractionNoFormatterException;
import org.junit.jupiter.api.Test;

/** Test the base {@link Message}. */
final class MessageTest {

    private final Formatter formatter = new Formatter(new Format("1", "22"));

    @Test
    void getTextWithoutFormatter() {
        var message = new Message("hello");

        assertEquals("hello", message.getText());
        assertEquals("hello", message.getFormattedText());
        assertFalse(message.hasFormatter());
    }

    @Test
    void getFormattedTextAppliesFormatter() {
        var message = new Message("hi", formatter);

        assertTrue(message.hasFormatter());
        assertSame(formatter, message.getFormatter());
        assertEquals("\033[1mhi\033[22m", message.getFormattedText());
    }

    @Test
    void getFormatterThrowsWhenAbsent() {
        assertThrows(
                CliInteractionNoFormatterException.class, () -> new Message("x").getFormatter());
    }

    @Test
    void withTextReturnsCopy() {
        var original = new Message("hello", formatter);

        var copy = original.withText("bye");

        assertNotSame(original, copy);
        assertEquals("bye", copy.getText());
        assertEquals("hello", original.getText());
    }

    @Test
    void withFormatterAndWithoutFormatterReturnCopies() {
        var original = new Message("x");

        var withFormatter = original.withFormatter(formatter);
        assertTrue(withFormatter.hasFormatter());
        assertFalse(original.hasFormatter());

        assertFalse(withFormatter.withoutFormatter().hasFormatter());
    }
}
