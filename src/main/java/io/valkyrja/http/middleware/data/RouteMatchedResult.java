/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.middleware.data;

import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import org.jspecify.annotations.Nullable;

public record RouteMatchedResult(RouteContract route, @Nullable ResponseContract response) {}
