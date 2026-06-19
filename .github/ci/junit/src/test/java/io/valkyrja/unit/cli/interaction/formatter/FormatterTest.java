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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.cli.interaction.format.Format;
import io.valkyrja.cli.interaction.formatter.Formatter;
import org.junit.jupiter.api.Test;

/** Test the base {@link Formatter}. */
final class FormatterTest {

    @Test
    void formatTextWithoutFormatsReturnsTextUnchanged() {
        assertEquals("plain", new Formatter().formatText("plain"));
    }

    @Test
    void formatTextWrapsTextInAnsiCodes() {
        var formatter = new Formatter(new Format("1", "22"), new Format("31", "39"));

        assertEquals("\033[1;31mhi\033[22;39m", formatter.formatText("hi"));
    }

    @Test
    void getFormatsReflectsConstructor() {
        var formatter = new Formatter(new Format("1", "22"));

        assertEquals(1, formatter.getFormats().size());
    }

    @Test
    void getFormatsIsUnmodifiable() {
        var formats = new Formatter(new Format("1", "22")).getFormats();

        assertThrows(UnsupportedOperationException.class, () -> formats.add(new Format("4", "24")));
    }

    @Test
    void withFormatsReturnsCopy() {
        var original = new Formatter(new Format("1", "22"));

        var copy = original.withFormats(new Format("4", "24"), new Format("5", "25"));

        assertNotSame(original, copy);
        assertEquals(2, copy.getFormats().size());
        assertEquals(1, original.getFormats().size());
    }
}
