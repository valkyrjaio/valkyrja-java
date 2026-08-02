/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.dispatch.data.abstract_;

import io.valkyrja.dispatch.data.contract.DispatchContract;
import java.util.Map;

/** Abstract base for all dispatch data objects. */
public abstract class Dispatch implements DispatchContract {

    @Override
    public abstract Map<String, Object> toMap();

    @Override
    public abstract String toString();
}
