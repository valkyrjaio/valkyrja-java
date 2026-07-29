/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.application.entry.netty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.netty.NettyHttp;
import org.junit.jupiter.api.Test;

/** Test the {@link NettyHttp} entry point. */
final class NettyHttpTest {

    @Test
    void isInstantiable() {
        assertNotNull(new NettyHttp());
    }
}
