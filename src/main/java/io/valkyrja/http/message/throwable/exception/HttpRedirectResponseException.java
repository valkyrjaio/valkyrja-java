/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.throwable.exception;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.RedirectResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;

public class HttpRedirectResponseException extends HttpResponseException {

    protected UriContract uri;

    public HttpRedirectResponseException(UriContract uri, StatusCode statusCode, HeaderCollectionContract headers, ResponseContract response) {
        super(
                statusCode != null ? statusCode : StatusCode.FOUND,
                "Redirect",
                headers != null ? headers : new HeaderCollection(),
                response != null ? response : RedirectResponse.createFromUri(
                        uri != null ? uri : new Uri("/"),
                        statusCode != null ? statusCode : StatusCode.FOUND,
                        headers != null ? headers : new HeaderCollection()
                )
        );

        this.uri = uri != null ? uri : new Uri("/");
    }

    public UriContract getUri() {
        return uri;
    }
}
