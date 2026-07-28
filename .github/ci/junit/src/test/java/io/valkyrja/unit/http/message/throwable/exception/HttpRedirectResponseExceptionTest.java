/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.throwable.exception;

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
