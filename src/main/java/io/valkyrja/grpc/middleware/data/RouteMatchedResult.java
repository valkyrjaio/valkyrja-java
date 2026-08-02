/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
