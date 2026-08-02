/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry.tomcat;

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
