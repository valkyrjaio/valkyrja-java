/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.contract;

/**
 * Contract for events that can halt listener propagation.
 *
 * <p>Equivalent to PSR-14's {@code StoppableEventInterface}.
 */
public interface StoppableEventContract {

    /**
     * Whether the event's propagation should stop.
     *
     * @return true if no further listeners should be called
     */
    boolean isPropagationStopped();
}
