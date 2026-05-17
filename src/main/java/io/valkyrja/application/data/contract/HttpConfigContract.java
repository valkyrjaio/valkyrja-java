/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.data.contract;

import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;

public interface HttpConfigContract extends ConfigContract {
    Integer port();

    List<Class<? extends RequestReceivedMiddlewareContract>> requestReceivedMiddleware();

    List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware();

    List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware();

    List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware();

    List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware();

    List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware();

    List<Class<? extends TerminatedMiddlewareContract>> terminatedMiddleware();
}
