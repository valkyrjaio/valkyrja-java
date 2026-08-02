/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.matcher.contract;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.routing.data.contract.RouteContract;
import org.jspecify.annotations.Nullable;

public interface MatcherContract {

    @Nullable RouteContract match(String path, RequestMethod requestMethod);

    @Nullable RouteContract matchStatic(String path, RequestMethod requestMethod);

    @Nullable RouteContract matchDynamic(String path, RequestMethod requestMethod);
}
