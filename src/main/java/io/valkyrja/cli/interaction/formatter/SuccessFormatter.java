/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.formatter;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;
import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.BackgroundColorFormat;
import io.valkyrja.cli.interaction.format.TextColorFormat;

public class SuccessFormatter extends Formatter {

    public SuccessFormatter() {
        super(new TextColorFormat(TextColor.LIGHT_WHITE), new BackgroundColorFormat(BackgroundColor.GREEN));
    }
}