/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.data.contract;

import io.valkyrja.type.data.Cast;
import org.jspecify.annotations.Nullable;

public interface ParameterContract {

    String getName();

    ParameterContract withName(String name);

    String getRegex();

    ParameterContract withRegex(String regex);

    boolean hasCast();

    Cast getCast();

    ParameterContract withCast(Cast cast);

    boolean isOptional();

    ParameterContract withIsOptional(boolean isOptional);

    boolean shouldCapture();

    ParameterContract withShouldCapture(boolean shouldCapture);

    @Nullable Object getDefault();

    ParameterContract withDefault(@Nullable Object defaultValue);

    @Nullable Object getValue();

    ParameterContract withValue(@Nullable Object value);
}
