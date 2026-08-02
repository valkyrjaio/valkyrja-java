/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.Referer;
import org.junit.jupiter.api.Test;

/** Test the {@link Referer}. */
final class RefererTest {

    @Test
    void usesRefererName() {
        var header = new Referer("/back");

        assertEquals(HeaderName.REFERER, header.getName());
        assertTrue(header.getHeaderLine().contains("/back"));
    }
}
