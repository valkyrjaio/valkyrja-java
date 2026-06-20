/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.uri.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import io.valkyrja.http.message.uri.enum_.Scheme;
import org.junit.jupiter.api.Test;

/** Test the {@link Scheme}. */
final class SchemeTest {

    @Test
    void exposesValuesAndStringForm() {
        assertEquals(3, Scheme.values().length);
        assertSame(Scheme.HTTPS, Scheme.valueOf("HTTPS"));
        assertEquals("", Scheme.EMPTY.getValue());
        assertEquals("http", Scheme.HTTP.getValue());
        assertEquals("https", Scheme.HTTPS.getValue());
    }
}
