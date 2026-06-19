/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.message.constant.HeaderValue;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link HeaderValue} constant holder. */
final class HeaderValueTest {

    @Test
    void exposesConstants() {
        assertEquals("Bearer", HeaderValue.BEARER);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<HeaderValue> constructor = HeaderValue.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
