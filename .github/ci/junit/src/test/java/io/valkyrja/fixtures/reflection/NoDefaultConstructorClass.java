/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.reflection;

/** Class without a no-arg constructor — reflection instantiation must fail. */
public final class NoDefaultConstructorClass {

    private final String value;

    public NoDefaultConstructorClass(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
