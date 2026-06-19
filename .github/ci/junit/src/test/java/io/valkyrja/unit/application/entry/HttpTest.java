/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.entry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.data.HttpConfig;
import io.valkyrja.application.entry.Http;
import org.junit.jupiter.api.Test;

/** Test the {@link Http} entry point. */
final class HttpTest {

    @Test
    void runBootstrapsAndHandlesARequest() {
        assertDoesNotThrow(() -> Http.run(new HttpConfig()));
    }

    @Test
    void isInstantiable() {
        assertNotNull(new Http());
    }
}
