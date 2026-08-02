/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
