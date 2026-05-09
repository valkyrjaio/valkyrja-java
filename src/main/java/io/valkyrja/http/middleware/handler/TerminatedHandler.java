/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.handler;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.handler.abstract_.Handler;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;

public class TerminatedHandler extends Handler<TerminatedMiddlewareContract> implements TerminatedHandlerContract {

    public TerminatedHandler(String... middleware) {
        super(middleware);
    }

    @Override
    public void terminated(ServerRequestContract request, ResponseContract response) {
        String next = this.next;
        if (next != null) {
            getMiddleware(next).terminated(request, response, this);
        }
    }
}