/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
