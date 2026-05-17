/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.middleware.requestreceived;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.RedirectResponse;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;

public class RedirectTrailingSlashMiddleware implements RequestReceivedMiddlewareContract {

    @Override
    public RequestReceivedResult requestReceived(
            ServerRequestContract request, RequestReceivedHandlerContract handler) {
        if (shouldRedirectRequest(request)) {
            UriContract uri = createBeforeRedirectUri(request.getUri());
            ResponseContract response = createBeforeRedirectResponse(uri);
            return new RequestReceivedResult(request, response);
        }

        return handler.requestReceived(request);
    }

    protected boolean shouldRedirectRequest(ServerRequestContract request) {
        String path = request.getUri().getPath();
        return !"/".equals(path) && path.endsWith("/");
    }

    protected UriContract createBeforeRedirectUri(UriContract uri) {
        return new Uri(
                uri.getScheme(),
                uri.getUsername(),
                uri.getPassword(),
                uri.getHost(),
                uri.getPort(),
                "/" + uri.getPath().replaceAll("^/+|/+$", ""),
                uri.getQuery(),
                uri.getFragment());
    }

    protected RedirectResponseContract createBeforeRedirectResponse(UriContract uri) {
        return RedirectResponse.createFromUri(uri, null, null);
    }
}
