/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry;

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
