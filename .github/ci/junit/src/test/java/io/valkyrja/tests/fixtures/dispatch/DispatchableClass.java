/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.dispatch;

/** Reflection target for dispatcher tests: static/instance methods, fields, and a constant. */
public class DispatchableClass {

    public static final String CONSTANT = "constant-value";

    public static String staticField = "static-field";

    public String instanceField = "instance-field";

    public static String staticMethod() {
        return "static-result";
    }

    public String instanceMethod() {
        return "instance-result";
    }

    public String echo(Object value) {
        return "echo:" + value;
    }

    public static String boom() {
        throw new IllegalStateException("kaboom");
    }

    /** Private — reflective invocation without access fails with IllegalAccessException. */
    @SuppressWarnings("unused")
    private String inaccessibleMethod() {
        return "inaccessible";
    }
}
