/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.factory.contract;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;

import java.util.Map;

public interface RoutingResponseFactoryContract {

    RedirectResponseContract createRouteRedirectResponse(String name, Map<String, Object> data, StatusCode statusCode, HeaderCollectionContract headers);
}
