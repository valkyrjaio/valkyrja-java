/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.event;

import io.valkyrja.event.contract.DispatchCollectableEventContract;
import java.util.ArrayList;
import java.util.List;

/** Event that collects the result of each listener dispatch. */
public final class DispatchCollectableEventFixture implements DispatchCollectableEventContract {

    private final List<Object> dispatches = new ArrayList<>();

    @Override
    public void addDispatch(Object result) {
        dispatches.add(result);
    }

    public List<Object> getDispatches() {
        return dispatches;
    }
}
