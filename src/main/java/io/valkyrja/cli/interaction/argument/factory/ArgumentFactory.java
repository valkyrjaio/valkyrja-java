/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.argument.factory;

import io.valkyrja.cli.interaction.argument.Argument;

public abstract class ArgumentFactory {

    public static Argument fromArg(String arg) {
        return new Argument(arg);
    }
}