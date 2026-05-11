/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.data.option;

import io.valkyrja.cli.routing.constant.OptionName;
import io.valkyrja.cli.routing.constant.OptionShortName;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import java.util.ArrayList;
import java.util.List;

public class SilentOptionParameter extends OptionParameter {

    public SilentOptionParameter() {
        super(OptionName.SILENT, "All output is suppressed.", "", "", List.of(OptionShortName.SILENT), List.of(), new ArrayList<>(), OptionMode.OPTIONAL, OptionValueMode.NONE);
    }
}
