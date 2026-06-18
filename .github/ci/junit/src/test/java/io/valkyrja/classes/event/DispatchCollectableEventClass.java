/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.event;

import io.valkyrja.event.contract.DispatchCollectableEventContract;
import java.util.ArrayList;
import java.util.List;

/** Event that collects the result of each listener dispatch. */
public final class DispatchCollectableEventClass implements DispatchCollectableEventContract {

    private final List<Object> dispatches = new ArrayList<>();

    @Override
    public void addDispatch(Object result) {
        dispatches.add(result);
    }

    public List<Object> getDispatches() {
        return dispatches;
    }
}