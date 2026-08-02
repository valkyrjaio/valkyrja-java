/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.throwable.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.throwable.exception.HttpRedirectResponseException;
import io.valkyrja.http.message.uri.Uri;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRedirectResponseException}. */
final class HttpRedirectResponseExceptionTest {

    @Test
    void buildsRedirectResponseWhenNoneProvided() {
        var exception =
                new HttpRedirectResponseException(
                        new Uri("/target"), null, new HeaderCollection(), null);

        assertEquals(StatusCode.FOUND, exception.getStatusCode());
        assertEquals("/target", exception.getUri().getPath());
        assertNotNull(exception.getResponse());
    }

    @Test
    void usesProvidedResponse() {
        var provided = new io.valkyrja.http.message.response.EmptyResponse();
        var exception =
                new HttpRedirectResponseException(
                        new Uri("/x"), StatusCode.FOUND, new HeaderCollection(), provided);

        assertNotNull(exception.getResponse());
    }

    @Test
    void defaultsUriToRootWhenNull() {
        var exception =
                new HttpRedirectResponseException(null, StatusCode.MOVED_PERMANENTLY, null, null);

        assertEquals("/", exception.getUri().getPath());
        assertEquals(StatusCode.MOVED_PERMANENTLY, exception.getStatusCode());
    }
}
