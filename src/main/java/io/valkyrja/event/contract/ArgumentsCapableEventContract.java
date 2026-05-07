/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.event.contract;

import java.util.Map;

/** Contract for events that accept runtime arguments after construction. */
public interface ArgumentsCapableEventContract {

    /**
     * Set the event arguments.
     *
     * @param arguments the arguments
     * @return this event (for fluent use)
     */
    ArgumentsCapableEventContract setArguments(Map<String, Object> arguments);
}
