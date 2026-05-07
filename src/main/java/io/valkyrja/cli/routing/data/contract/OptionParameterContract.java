/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data.contract;

import io.valkyrja.cli.interaction.option.contract.OptionContract;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import java.util.List;

public interface OptionParameterContract extends ParameterContract {

    List<String> getShortNames();

    OptionParameterContract withShortNames(String... shortNames);

    OptionParameterContract withAddedShortNames(String... shortNames);

    OptionMode getMode();

    OptionParameterContract withMode(OptionMode mode);

    OptionValueMode getValueMode();

    OptionParameterContract withValueMode(OptionValueMode valueMode);

    boolean hasValueDisplayName();

    String getValueDisplayName();

    OptionParameterContract withValueDisplayName(String valueName);

    List<String> getValidValues();

    OptionParameterContract withValidValues(String... validValues);

    OptionParameterContract withAddedValidValues(String... validValues);

    boolean hasDefaultValue();

    String getDefaultValue();

    OptionParameterContract withDefaultValue(String defaultValue);

    List<OptionContract> getOptions();

    OptionParameterContract withOptions(OptionContract... options);

    OptionParameterContract withAddedOptions(OptionContract... options);
}
