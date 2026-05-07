/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.contract;

/** Contract for events that collect the return values of their dispatched listeners. */
public interface DispatchCollectableEventContract {

    /**
     * Add a dispatch result from a listener invocation.
     *
     * @param result the return value of a listener
     */
    void addDispatch(Object result);
}
