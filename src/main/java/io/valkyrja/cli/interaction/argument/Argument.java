/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.argument;

import io.valkyrja.cli.interaction.argument.contract.ArgumentContract;

public class Argument implements ArgumentContract {

    protected String value;

    public Argument(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ArgumentContract withValue(String value) {
        Argument copy = new Argument(this.value);
        copy.value = value;
        return copy;
    }
}
