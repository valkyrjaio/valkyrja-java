/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.routing.constant.OptionShortName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link OptionShortName} constant holder. */
final class OptionShortNameTest {

    @Test
    void exposesConstants() {
        assertEquals("h", OptionShortName.HELP);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<OptionShortName> constructor = OptionShortName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
