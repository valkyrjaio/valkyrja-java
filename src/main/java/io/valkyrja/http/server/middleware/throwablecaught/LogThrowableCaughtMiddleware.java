/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.middleware.throwablecaught;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.log.logger.contract.LoggerContract;

import java.util.Map;

public class LogThrowableCaughtMiddleware implements ThrowableCaughtMiddlewareContract {

    protected LoggerContract logger;

    public LogThrowableCaughtMiddleware(LoggerContract logger) {
        this.logger = logger;
    }

    @Override
    public ResponseContract throwableCaught(ServerRequestContract request, ResponseContract response, Throwable throwable, ThrowableCaughtHandlerContract handler) {
        String url        = request.getUri().getPath();
        String logMessage = "Http Server Error\nUrl: " + url;

        logger.throwable(throwable, logMessage, Map.of());

        return handler.throwableCaught(request, response, throwable);
    }
}