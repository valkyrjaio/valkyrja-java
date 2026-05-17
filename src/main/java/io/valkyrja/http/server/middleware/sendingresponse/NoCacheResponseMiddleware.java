/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.middleware.sendingresponse;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;

public class NoCacheResponseMiddleware implements SendingResponseMiddlewareContract {

    protected String[] expires = {"Sun, 01 Jan 2014 00:00:00 GMT"};
    protected String[] cacheControl = {
        "no-store", "no-cache", "must-revalidate", "post-check=0", "pre-check=0"
    };
    protected String[] pragma = {"no-cache"};

    @Override
    public ResponseContract sendingResponse(
            ServerRequestContract request,
            ResponseContract response,
            SendingResponseHandlerContract handler) {
        HeaderCollectionContract headers = response.getHeaders();
        headers =
                headers.withHeader(new Header(HeaderName.EXPIRES, (Object[]) expires))
                        .withHeader(new Header(HeaderName.CACHE_CONTROL, (Object[]) cacheControl))
                        .withHeader(new Header(HeaderName.PRAGMA, (Object[]) pragma));

        return handler.sendingResponse(request, (ResponseContract) response.withHeaders(headers));
    }
}
