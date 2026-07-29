/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.enum_.SameSite;
import org.junit.jupiter.api.Test;

/** Test the {@link SameSite} enum. */
final class SameSiteTest {

    @Test
    void getValue() {
        assertEquals("lax", SameSite.LAX.getValue());
        assertEquals("strict", SameSite.STRICT.getValue());
        assertEquals("none", SameSite.NONE.getValue());
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (SameSite s : SameSite.values()) {
            assertSame(s, SameSite.valueOf(s.name()));
        }
    }
}
