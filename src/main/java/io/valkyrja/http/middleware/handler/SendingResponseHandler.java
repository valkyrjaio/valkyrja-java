/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.handler;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;

public class SendingResponseHandler extends Handler<SendingResponseMiddlewareContract> implements SendingResponseHandlerContract {

    @SafeVarargs
    public SendingResponseHandler(ContainerContract container, Class<? extends SendingResponseMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public ResponseContract sendingResponse(ServerRequestContract request, ResponseContract response) {
        Class<? extends SendingResponseMiddlewareContract> next = this.next;
        return next != null
                ? getMiddleware(next).sendingResponse(request, response, this)
                : response;
    }
}
