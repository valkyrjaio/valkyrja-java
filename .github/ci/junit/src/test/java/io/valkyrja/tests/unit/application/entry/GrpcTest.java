/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.Grpc;
import org.junit.jupiter.api.Test;

/** Test the {@link Grpc} entry point. */
final class GrpcTest {

    @Test
    void isInstantiable() {
        assertNotNull(new Grpc());
    }
}
