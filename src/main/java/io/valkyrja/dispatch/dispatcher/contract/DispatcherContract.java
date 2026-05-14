/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.dispatcher.contract;

import io.valkyrja.dispatch.data.contract.DispatchContract;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/** Contract for the dispatch dispatcher. */
public interface DispatcherContract {

    /**
     * Dispatch a dispatch contract with no extra arguments.
     *
     * @param dispatch the dispatch to execute
     * @return the result of the dispatch
     */
    @Nullable Object dispatch(DispatchContract dispatch);

    /**
     * Dispatch a dispatch contract with extra arguments.
     *
     * @param dispatch the dispatch to execute
     * @param arguments extra arguments to pass (merged with dispatch's own arguments)
     * @return the result of the dispatch
     */
    @Nullable Object dispatch(DispatchContract dispatch, Map<String, Object> arguments);
}
