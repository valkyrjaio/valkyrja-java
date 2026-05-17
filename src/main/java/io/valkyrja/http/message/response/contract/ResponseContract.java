/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response.contract;

import io.valkyrja.http.message.contract.MessageContract;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.header.value.contract.CookieContract;
import org.jspecify.annotations.Nullable;

public interface ResponseContract extends MessageContract {

    static ResponseContract create(
            @Nullable String content,
            @Nullable StatusCode statusCode,
            @Nullable HeaderCollectionContract headers) {
        throw new UnsupportedOperationException("create must be implemented by the concrete class");
    }

    StatusCode getStatusCode();

    ResponseContract withStatusCode(StatusCode code);

    String getReasonPhrase();

    ResponseContract withReasonPhrase(String reasonPhrase);

    ResponseContract withCookie(CookieContract cookie);

    ResponseContract withoutCookie(CookieContract cookie);

    ResponseContract sendHttpLine();

    ResponseContract sendHeaders();

    ResponseContract sendBody();

    ResponseContract send();
}
