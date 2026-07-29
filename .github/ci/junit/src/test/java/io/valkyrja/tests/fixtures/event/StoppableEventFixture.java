/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.event;

import io.valkyrja.event.contract.DispatchCollectableEventContract;
import io.valkyrja.event.contract.StoppableEventContract;
import java.util.ArrayList;
import java.util.List;

/** Event that collects dispatches and always reports propagation as stopped. */
public final class StoppableEventFixture
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
        return true;
    }
}
