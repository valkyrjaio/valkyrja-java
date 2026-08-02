/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry.netty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.netty.NettyGrpc;
import org.junit.jupiter.api.Test;

/** Test the {@link NettyGrpc} entry point. */
final class NettyGrpcTest {

    @Test
    void isInstantiable() {
        assertNotNull(new NettyGrpc());
    }
}
