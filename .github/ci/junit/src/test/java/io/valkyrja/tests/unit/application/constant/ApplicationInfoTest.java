/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.application.constant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.constant.ApplicationInfo;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

final class ApplicationInfoTest {

    @Test
    void constantsAreDefined() {
        assertNotNull(ApplicationInfo.VERSION);
        assertNotNull(ApplicationInfo.VERSION_BUILD_DATE_TIME);
        assertNotNull(ApplicationInfo.ASCII);
    }

    @Test
    void privateConstructorIsInvocable() throws Exception {
        Constructor<ApplicationInfo> constructor = ApplicationInfo.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
