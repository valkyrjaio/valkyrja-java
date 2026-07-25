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
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;

/** Middleware run after the response has been fully written to the wire. */
public interface ResponseSentMiddlewareContract {

    void responseSent(
            ServiceCallContract call,
            ServiceResponseContract response,
            ResponseSentHandlerContract handler);
}
