/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.enum_;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class InvalidReferenceModeTest {

    @Test
    void valuesAndValueOf() {
        assertEquals(2, InvalidReferenceMode.values().length);
        assertEquals(
                InvalidReferenceMode.THROW_EXCEPTION,
                InvalidReferenceMode.valueOf("THROW_EXCEPTION"));
        assertEquals(
                InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION,
                InvalidReferenceMode.valueOf("NEW_INSTANCE_OR_THROW_EXCEPTION"));
    }
}
