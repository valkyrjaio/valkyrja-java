/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.option.contract;

import io.valkyrja.cli.interaction.enum_.OptionType;

public interface OptionContract {

    String getName();

    OptionContract withName(String name);

    OptionType getType();

    OptionContract withType(OptionType type);

    boolean hasValue();

    String getValue();

    OptionContract withValue(String value);

    OptionContract withoutValue();
}
