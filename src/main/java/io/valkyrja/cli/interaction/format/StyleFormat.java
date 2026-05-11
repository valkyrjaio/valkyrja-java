/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.format;

import io.valkyrja.cli.interaction.enum_.Style;

public class StyleFormat extends Format {

    public StyleFormat(Style style) {
        super(String.valueOf(style.value), String.valueOf(style.getDefault()));
    }
}
