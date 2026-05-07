/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.enum_;

public enum Style {
    BOLD(1),
    UNDERSCORE(4),
    BLINK(5),
    INVERSE(7),
    CONCEAL(8),
    ;

    public final int value;

    Style(int value) {
        this.value = value;
    }

    public int getDefault() {
        return switch (this) {
            case BOLD -> 22;
            case UNDERSCORE -> 24;
            case BLINK -> 25;
            case INVERSE -> 27;
            case CONCEAL -> 28;
        };
    }
}
