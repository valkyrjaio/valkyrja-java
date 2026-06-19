/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.RequestMethod;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestMethod} enum. */
final class RequestMethodTest {

    @Test
    void getValue() {
        assertEquals("GET", RequestMethod.GET.getValue());
    }

    @Test
    void allExcludesAny() {
        assertEquals(9, RequestMethod.all().size());
        assertTrue(RequestMethod.all().contains(RequestMethod.GET));
    }

    @Test
    void fromIsCaseInsensitiveAndRejectsUnknown() {
        assertSame(RequestMethod.POST, RequestMethod.from("post"));
        assertThrows(IllegalArgumentException.class, () -> RequestMethod.from("FETCH"));
    }

    @Test
    void valueOfResolvesEachConstant() {
        for (RequestMethod m : RequestMethod.values()) {
            assertSame(m, RequestMethod.valueOf(m.name()));
        }
    }
}
