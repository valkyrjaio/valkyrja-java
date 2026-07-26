/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.entry.jetty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.jetty.JettyGrpc;
import org.junit.jupiter.api.Test;

/** Test the {@link JettyGrpc} entry point. */
final class JettyGrpcTest {

    @Test
    void isInstantiable() {
        assertNotNull(new JettyGrpc());
    }
}
