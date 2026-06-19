/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.server.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.cli.server.constant.CliCommandName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link CliCommandName} constant holder. */
final class CliCommandNameTest {

    @Test
    void exposesConstants() {
        assertEquals("help", CliCommandName.HELP);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<CliCommandName> constructor = CliCommandName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
