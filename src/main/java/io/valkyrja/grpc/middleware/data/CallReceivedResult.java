/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.data;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of the {@code CallReceived} stage: either the (possibly updated) call to continue
 * routing, or a response that short-circuits the pipeline.
 */
public record CallReceivedResult(
        ServiceCallContract call, @Nullable ServiceResponseContract response) {}
