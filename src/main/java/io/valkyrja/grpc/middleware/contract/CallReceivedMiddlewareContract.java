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
import io.valkyrja.grpc.middleware.data.CallReceivedResult;
import io.valkyrja.grpc.middleware.handler.contract.CallReceivedHandlerContract;

/** Middleware run once per call before routing. Always runs. */
public interface CallReceivedMiddlewareContract {

    CallReceivedResult callReceived(ServiceCallContract call, CallReceivedHandlerContract handler);
}
