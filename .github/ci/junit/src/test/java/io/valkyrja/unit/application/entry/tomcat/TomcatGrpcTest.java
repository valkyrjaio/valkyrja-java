/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.entry.tomcat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.tomcat.TomcatGrpc;
import org.junit.jupiter.api.Test;

/** Test the {@link TomcatGrpc} entry point. */
final class TomcatGrpcTest {

    @Test
    void isInstantiable() {
        assertNotNull(new TomcatGrpc());
    }
}
