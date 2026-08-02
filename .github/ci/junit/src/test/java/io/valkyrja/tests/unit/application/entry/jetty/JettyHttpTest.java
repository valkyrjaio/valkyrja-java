/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry.jetty;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.jetty.JettyHttp;
import org.junit.jupiter.api.Test;

/** Test the {@link JettyHttp} entry point. */
final class JettyHttpTest {

    @Test
    void isInstantiable() {
        assertNotNull(new JettyHttp());
    }
}
