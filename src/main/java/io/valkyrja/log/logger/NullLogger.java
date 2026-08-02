/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.log.logger;

import io.valkyrja.log.logger.abstract_.Logger;
import java.util.Map;

public class NullLogger extends Logger {

    @Override
    public void debug(String message, Map<String, Object> context) {}

    @Override
    public void info(String message, Map<String, Object> context) {}

    @Override
    public void notice(String message, Map<String, Object> context) {}

    @Override
    public void warning(String message, Map<String, Object> context) {}

    @Override
    public void error(String message, Map<String, Object> context) {}

    @Override
    public void critical(String message, Map<String, Object> context) {}

    @Override
    public void alert(String message, Map<String, Object> context) {}

    @Override
    public void emergency(String message, Map<String, Object> context) {}

    @Override
    public void throwable(Throwable throwable, String message, Map<String, Object> context) {}
}
