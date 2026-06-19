/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.routing.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.routing.constant.Regex;
import org.junit.jupiter.api.Test;

/** Test the {@link Regex} constants holder. */
final class RegexTest {

    @Test
    void exposesRegexConstants() {
        assertEquals("\\d+", Regex.NUM);
        assertEquals("\\d+", Regex.ID);
    }

    @Test
    void isInstantiableBySubclass() {
        assertNotNull(new Regex() {});
    }
}