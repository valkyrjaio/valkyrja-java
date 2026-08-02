/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.event;

import io.valkyrja.event.contract.DispatchCollectableEventContract;
import io.valkyrja.event.contract.StoppableEventContract;
import java.util.ArrayList;
import java.util.List;

/**
 * A stoppable event that never stops propagation — exercises the {@code isPropagationStopped()}
 * false branch of the dispatch loop, where every listener still runs.
 */
public final class NonStoppingStoppableEventFixture
        implements DispatchCollectableEventContract, StoppableEventContract {

    private final List<Object> dispatches = new ArrayList<>();

    @Override
    public void addDispatch(Object result) {
        dispatches.add(result);
    }

    public List<Object> getDispatches() {
        return dispatches;
    }

    @Override
    public boolean isPropagationStopped() {
        return false;
    }
}
