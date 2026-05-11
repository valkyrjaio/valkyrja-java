/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.formatter;

import io.valkyrja.cli.interaction.enum_.TextColor;
import io.valkyrja.cli.interaction.format.TextColorFormat;

public class HighlightedTextFormatter extends Formatter {

    public HighlightedTextFormatter() {
        super(new TextColorFormat(TextColor.YELLOW));
    }
}
