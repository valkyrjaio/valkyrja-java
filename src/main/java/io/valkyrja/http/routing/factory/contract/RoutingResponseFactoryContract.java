/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.factory.contract;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import java.util.Map;

public interface RoutingResponseFactoryContract {

    RedirectResponseContract createRouteRedirectResponse(
            String name,
            Map<String, Object> data,
            StatusCode statusCode,
            HeaderCollectionContract headers);
}
