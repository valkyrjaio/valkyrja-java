/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data.contract;

/** Contract for dispatches that resolve a Java static final field constant. */
public interface ConstantDispatchContract extends DispatchContract {

    /**
     * Get the constant (field) name.
     *
     * @return the constant name
     */
    String getConstant();

    /**
     * Return a new instance with the given constant name.
     *
     * @param constant the constant name
     * @return new dispatch
     */
    ConstantDispatchContract withConstant(String constant);

    /**
     * Whether this dispatch has a class context.
     *
     * @return true if a class is set
     */
    boolean hasClassName();

    /**
     * Get the class context for this constant.
     *
     * @return the class object
     * @throws io.valkyrja.dispatch.throwable.exception.DispatchNoClassException if no class is set
     */
    Class<?> getClassName();

    /**
     * Return a new instance with the given class.
     *
     * @param className the class object
     * @return new dispatch
     */
    ConstantDispatchContract withClassName(Class<?> className);

    /**
     * Return a new instance with the class cleared.
     *
     * @return new dispatch
     */
    ConstantDispatchContract withoutClassName();
}
