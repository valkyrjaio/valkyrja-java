/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.type.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.type.data.Cast;
import org.junit.jupiter.api.Test;

/** Test the {@link Cast} type descriptor. */
final class CastTest {

    @Test
    void fullConstructor() {
        var cast = new Cast("int", false, true);

        assertEquals("int", cast.getType());
        assertFalse(cast.isConvert());
        assertTrue(cast.isArray());
    }

    @Test
    void typeOnlyConstructorDefaultsToConvertingNonArray() {
        var cast = new Cast("string");

        assertEquals("string", cast.getType());
        assertTrue(cast.isConvert());
        assertFalse(cast.isArray());
    }
}
