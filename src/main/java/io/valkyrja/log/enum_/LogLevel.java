/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.log.enum_;

public enum LogLevel {
    DEBUG("debug"),
    INFO("info"),
    NOTICE("notice"),
    WARNING("warning"),
    ERROR("error"),
    CRITICAL("critical"),
    ALERT("alert"),
    EMERGENCY("emergency");

    private final String value;

    LogLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
