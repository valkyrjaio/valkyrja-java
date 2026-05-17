/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response.contract;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.uri.contract.UriContract;

public interface RedirectResponseContract extends ResponseContract {

    static RedirectResponseContract createFromUri(
            UriContract uri, StatusCode statusCode, HeaderCollectionContract headers) {
        throw new UnsupportedOperationException(
                "createFromUri must be implemented by the concrete class");
    }

    UriContract getUri();

    RedirectResponseContract withUri(UriContract uri);

    RedirectResponseContract secure(String path, ServerRequestContract request);

    RedirectResponseContract back(ServerRequestContract request);
}
