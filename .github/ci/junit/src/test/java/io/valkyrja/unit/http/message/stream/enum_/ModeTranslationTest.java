/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.stream.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.stream.enum_.ModeTranslation;
import org.junit.jupiter.api.Test;

/** Test the {@link ModeTranslation} enum. */
final class ModeTranslationTest {

    @Test
    void getValue() {
        assertEquals("", ModeTranslation.NONE.getValue());
        assertEquals("t", ModeTranslation.WINDOWS.getValue());
        assertEquals("b", ModeTranslation.BINARY_SAFE.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (ModeTranslation t : ModeTranslation.values()) {
            assertSame(t, ModeTranslation.valueOf(t.name()));
        }
    }
}
