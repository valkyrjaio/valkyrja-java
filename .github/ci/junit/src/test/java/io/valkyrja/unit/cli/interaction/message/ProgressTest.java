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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Progress;
import org.junit.jupiter.api.Test;

/** Test the {@link Progress} message. */
final class ProgressTest {

    @Test
    void singleArgConstructorDefaults() {
        var progress = new Progress("loading");

        assertEquals("loading", progress.getText());
        assertFalse(progress.isComplete());
        assertEquals(0, progress.getPercentage());
    }

    @Test
    void withMethodsReturnImmutableCopies() {
        var original = new Progress("loading");

        var complete = original.withIsComplete(true);
        var advanced = original.withPercentage(50);

        assertNotSame(original, complete);
        assertTrue(complete.isComplete());
        assertEquals(50, advanced.getPercentage());
        assertFalse(original.isComplete());
        assertEquals(0, original.getPercentage());
    }
}