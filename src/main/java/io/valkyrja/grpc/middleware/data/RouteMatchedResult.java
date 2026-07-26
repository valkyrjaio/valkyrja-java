/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.data;

import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import org.jspecify.annotations.Nullable;

/**
 * The outcome of the {@code RouteMatched} stage: either the (possibly updated) route to dispatch to
 * the handler, or a response that short-circuits the pipeline.
 */
public record RouteMatchedResult(RouteContract route, @Nullable ServiceResponseContract response) {}
