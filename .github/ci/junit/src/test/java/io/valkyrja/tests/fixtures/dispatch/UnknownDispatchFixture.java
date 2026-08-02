/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.dispatch;

import io.valkyrja.dispatch.data.contract.DispatchContract;
import java.util.Map;

/** A dispatch type the dispatcher does not recognize — triggers the unknown-type branch. */
public final class UnknownDispatchFixture implements DispatchContract {

    @Override
    public Map<String, Object> toMap() {
        return Map.of();
    }

    @Override
    public String toString() {
        return "unknown";
    }
}
