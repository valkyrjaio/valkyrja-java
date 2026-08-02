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
import io.valkyrja.http.message.header.ContentType;
import org.junit.jupiter.api.Test;

/** Test the {@link ContentType}. */
final class ContentTypeTest {

    @Test
    void usesContentTypeNameAndValue() {
        var header = new ContentType("text/html");

        assertEquals(HeaderName.CONTENT_TYPE, header.getName());
        assertTrue(header.getHeaderLine().contains("text/html"));
    }
}
