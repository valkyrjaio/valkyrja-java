/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
