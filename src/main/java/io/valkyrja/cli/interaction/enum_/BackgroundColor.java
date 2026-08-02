/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.interaction.enum_;

public enum BackgroundColor {
    BLACK(40),
    RED(41),
    GREEN(42),
    YELLOW(43),
    BLUE(44),
    MAGENTA(45),
    CYAN(46),
    WHITE(47),
    DARK_GRAY(100),
    LIGHT_RED(101),
    LIGHT_GREEN(102),
    LIGHT_YELLOW(103),
    LIGHT_BLUE(104),
    LIGHT_MAGENTA(105),
    LIGHT_CYAN(106),
    LIGHT_WHITE(107),
    ;

    public final int value;

    BackgroundColor(int value) {
        this.value = value;
    }

    public int getDefault() {
        return 49;
    }
}
