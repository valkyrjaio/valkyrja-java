/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.format;

import io.valkyrja.cli.interaction.enum_.BackgroundColor;

public class BackgroundColorFormat extends Format {

    public BackgroundColorFormat(BackgroundColor backgroundColor) {
        super(String.valueOf(backgroundColor.value), String.valueOf(backgroundColor.getDefault()));
    }
}