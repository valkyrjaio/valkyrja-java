/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data.contract;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Contract for dispatches that target a callable (lambda/function). */
public interface CallableDispatchContract extends DispatchContract {

    /**
     * Get the callable to invoke.
     *
     * @return the callable function
     */
    Function<Object[], Object> getCallable();

    /**
     * Return a new instance with the given callable.
     *
     * @param callable the callable function
     * @return new dispatch
     */
    CallableDispatchContract withCallable(Function<Object[], Object> callable);

    /**
     * Get the arguments to pass at invocation time.
     *
     * @return arguments map
     */
    Map<String, Object> getArguments();

    /**
     * Return a new instance with the given arguments.
     *
     * @param arguments the arguments
     * @return new dispatch
     */
    CallableDispatchContract withArguments(Map<String, Object> arguments);

    /**
     * Get the dependency types to resolve from the container before invocation.
     *
     * @return list of dependency types
     */
    List<Class<?>> getDependencies();

    /**
     * Return a new instance with the given dependencies.
     *
     * @param dependencies the dependency types
     * @return new dispatch
     */
    CallableDispatchContract withDependencies(List<Class<?>> dependencies);
}
