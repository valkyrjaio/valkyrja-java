/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.type.contract;

import java.util.function.UnaryOperator;
import org.jspecify.annotations.Nullable;

public interface TypeContract {

    @Nullable Object asValue();

    @Nullable Object asFlatValue();

    TypeContract modify(UnaryOperator<@Nullable Object> closure);
}
