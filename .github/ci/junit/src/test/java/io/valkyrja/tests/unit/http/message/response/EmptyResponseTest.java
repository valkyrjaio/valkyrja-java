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
import io.valkyrja.http.message.response.EmptyResponse;
import org.junit.jupiter.api.Test;

/** Test the {@link EmptyResponse}. */
final class EmptyResponseTest {

    @Test
    void defaultConstructorIsNoContentWithEmptyBody() {
        var response = new EmptyResponse();

        assertEquals(StatusCode.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().toString().isEmpty());
    }

    @Test
    void headerConstructorKeepsNoContentStatus() {
        var response = new EmptyResponse(new HeaderCollection());

        assertEquals(StatusCode.NO_CONTENT, response.getStatusCode());
    }
}
