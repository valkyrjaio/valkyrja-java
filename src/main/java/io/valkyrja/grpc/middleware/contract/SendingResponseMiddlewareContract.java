/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;

/** Middleware run before the adapter writes the response to the wire. Always runs. */
public interface SendingResponseMiddlewareContract {

    ServiceResponseContract sendingResponse(
            ServiceCallContract call,
            ServiceResponseContract response,
            SendingResponseHandlerContract handler);
}
