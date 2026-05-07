/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.support;

public class Exiter {

    protected static boolean exit = true;

    public static void freeze() {
        exit = false;
    }

    public static void unfreeze() {
        exit = true;
    }

    public static void exit(int code) {
        if (exit) {
            System.exit(code);
        } else {
            frozenCallback(code);
        }
    }

    public static void exit() {
        exit(0);
    }

    public static void frozenCallback(int code) {
        System.out.print(code);
    }
}