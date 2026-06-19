/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.interaction.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.cli.interaction.format.Format;
import org.junit.jupiter.api.Test;

/** Test the {@link Format} value object. */
final class FormatTest {

    @Test
    void getters() {
        var format = new Format("1", "22");

        assertEquals("1", format.getSetCode());
        assertEquals("22", format.getUnsetCode());
    }

    @Test
    void withMethodsReturnImmutableCopies() {
        var original = new Format("1", "22");

        var withSet = original.withSetCode("4");
        var withUnset = original.withUnsetCode("24");

        assertNotSame(original, withSet);
        assertEquals("4", withSet.getSetCode());
        assertEquals("24", withUnset.getUnsetCode());
        // original unchanged
        assertEquals("1", original.getSetCode());
        assertEquals("22", original.getUnsetCode());
    }
}
