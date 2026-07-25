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
import io.valkyrja.http.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;

public class ResponseSentHandler extends Handler<ResponseSentMiddlewareContract>
        implements ResponseSentHandlerContract {

    @SafeVarargs
    public ResponseSentHandler(
            ContainerContract container,
            Class<? extends ResponseSentMiddlewareContract>... middleware) {
        super(container, middleware);
    }

    @Override
    public void responseSent(ServerRequestContract request, ResponseContract response) {
        Class<? extends ResponseSentMiddlewareContract> next = this.next;
        if (next != null) {
            getMiddleware(next).responseSent(request, response, this);
        }
    }
}
