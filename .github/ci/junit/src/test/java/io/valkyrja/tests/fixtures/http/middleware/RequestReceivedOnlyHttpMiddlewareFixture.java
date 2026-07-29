/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.http.middleware;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;

/**
 * Implements only {@link RequestReceivedMiddlewareContract} — none of the route-matched /
 * dispatched / throwable-caught / sending-response / response-sent contracts the route collector
 * checks — to exercise the {@code isAssignableFrom} false branches.
 */
public final class RequestReceivedOnlyHttpMiddlewareFixture
        implements RequestReceivedMiddlewareContract {

    @Override
    public RequestReceivedResult requestReceived(
            ServerRequestContract request, RequestReceivedHandlerContract handler) {
        return handler.requestReceived(request);
    }
}
