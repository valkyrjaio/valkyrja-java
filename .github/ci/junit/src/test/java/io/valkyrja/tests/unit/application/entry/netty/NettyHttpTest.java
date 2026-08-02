/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
