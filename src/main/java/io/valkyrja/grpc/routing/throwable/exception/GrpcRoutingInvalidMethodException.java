/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.throwable.exception;

import io.valkyrja.grpc.routing.throwable.exception.abstract_.GrpcRoutingRuntimeException;

public class GrpcRoutingInvalidMethodException extends GrpcRoutingRuntimeException {

    public GrpcRoutingInvalidMethodException(String message) {
        super(message);
    }

    public GrpcRoutingInvalidMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
