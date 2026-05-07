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

/** Contract for dispatches that target a class. */
public interface ClassDispatchContract extends DispatchContract {

    /**
     * Get the target class.
     *
     * @return the class object
     */
    Class<?> getClassName();

    /**
     * Return a new instance with the given class.
     *
     * @param className the class object
     * @return new dispatch
     */
    ClassDispatchContract withClassName(Class<?> className);

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
    ClassDispatchContract withArguments(Map<String, Object> arguments);

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
    ClassDispatchContract withDependencies(List<Class<?>> dependencies);
}
