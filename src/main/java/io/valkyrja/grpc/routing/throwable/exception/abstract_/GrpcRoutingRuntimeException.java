/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.throwable.exception.abstract_;

import io.valkyrja.grpc.routing.throwable.contract.GrpcRoutingThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

public abstract class GrpcRoutingRuntimeException extends GrpcRuntimeException
        implements GrpcRoutingThrowable {

    protected GrpcRoutingRuntimeException(String message) {
        super(message);
    }

    protected GrpcRoutingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
