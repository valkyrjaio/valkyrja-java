/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.dispatcher.contract;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;

/**
 * Resolves an inbound call to a {@code Route} via a direct service-map lookup and dispatches it.
 * The component keeps the {@code Router} name for consistency with HTTP and CLI; only the
 * resolution strategy (map lookup, no pattern matching) differs.
 */
public interface RouterContract {

    ServiceResponseContract dispatch(ServiceCallContract call);
}
