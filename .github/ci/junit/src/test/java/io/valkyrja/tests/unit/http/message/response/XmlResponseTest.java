/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.XmlResponse;
import org.junit.jupiter.api.Test;

/** Test the {@link XmlResponse}. */
final class XmlResponseTest {

    @Test
    void setsXmlContentTypeAndBody() {
        var response = new XmlResponse("<x/>", StatusCode.OK, new HeaderCollection());

        assertEquals("<x/>", response.getBody().toString());
        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("xml"));
    }

    @Test
    void defaultConstructorInjectsContentType() {
        assertTrue(new XmlResponse().getHeaders().has("content-type"));
    }
}
