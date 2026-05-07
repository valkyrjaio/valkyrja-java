/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data.contract;

/** Contract for dispatches that access a class field (property). */
public interface PropertyDispatchContract extends ClassDispatchContract {

    /**
     * Get the field name.
     *
     * @return the field name
     */
    String getProperty();

    /**
     * Return a new instance with the given field name.
     *
     * @param property the field name
     * @return new dispatch
     */
    PropertyDispatchContract withProperty(String property);

    /**
     * Whether the field should be accessed statically.
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
    PropertyDispatchContract withIsStatic(boolean isStatic);
}
