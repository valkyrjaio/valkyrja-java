/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.factory;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.routing.factory.contract.RoutingResponseFactoryContract;
import io.valkyrja.http.routing.url.contract.UrlContract;

import java.util.Map;

public class RoutingResponseFactory implements RoutingResponseFactoryContract {

    protected ResponseFactoryContract responseFactory;
    protected UrlContract url;

    public RoutingResponseFactory(ResponseFactoryContract responseFactory, UrlContract url) {
        this.responseFactory = responseFactory;
        this.url = url;
    }

    @Override
    public RedirectResponseContract createRouteRedirectResponse(String name, Map<String, Object> data, StatusCode statusCode, HeaderCollectionContract headers) {
        String url = this.url.getUrl(name, data);
        return responseFactory.createRedirectResponse(url, statusCode, headers);
    }
}