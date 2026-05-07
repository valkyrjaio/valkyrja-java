/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data.contract;

/** Contract for dispatches that target a class method. */
public interface MethodDispatchContract extends ClassDispatchContract {

    /**
     * Get the method name.
     *
     * @return the method name
     */
    String getMethod();

    /**
     * Return a new instance with the given method name.
     *
     * @param method the method name
     * @return new dispatch
     */
    MethodDispatchContract withMethod(String method);

    /**
     * Whether the method should be invoked statically.
     *
     * @return true if static
     */
    boolean isStatic();

    /**
     * Return a new instance with the given static flag.
     *
     * @param isStatic true if static
     * @return new dispatch
     */
    MethodDispatchContract withIsStatic(boolean isStatic);
}
