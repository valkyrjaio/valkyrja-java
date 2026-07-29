/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.routing.constant.OptionName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionName} constant holder. */
final class OptionNameTest {

    @Test
    void exposesConstants() {
        assertEquals("help", OptionName.HELP);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<OptionName> constructor = OptionName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
