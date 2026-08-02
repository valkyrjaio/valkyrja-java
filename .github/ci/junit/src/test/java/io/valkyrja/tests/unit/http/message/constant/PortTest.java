/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.constant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.Port;
import org.junit.jupiter.api.Test;

/** Test the {@link Port} constant holder. */
final class PortTest {

    @Test
    void isValidChecksRange() {
        assertTrue(Port.isValid(80));
        assertFalse(Port.isValid(0));
        assertFalse(Port.isValid(70000));
    }
}
