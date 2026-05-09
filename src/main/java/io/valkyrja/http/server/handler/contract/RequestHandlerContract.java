/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.handler.contract;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;

public interface RequestHandlerContract {

    ResponseContract handle(ServerRequestContract request);

    RequestHandlerContract send(ResponseContract response);

    void terminate(ServerRequestContract request, ResponseContract response);

    void run(ServerRequestContract request);
}
