/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.log.logger.abstract_;

import io.valkyrja.log.enum_.LogLevel;
import io.valkyrja.log.logger.contract.LoggerContract;
import io.valkyrja.log.throwable.exception.LogInvalidLogLevelException;
import java.util.Map;

public abstract class Logger implements LoggerContract {

    @Override
    public void log(LogLevel level, String message, Map<String, Object> context) {
        if (level == null) {
            throw new LogInvalidLogLevelException(
                    "Invalid log level passed. Expecting instance of " + LogLevel.class.getName());
        }

        switch (level) {
            case ALERT -> alert(message, context);
            case DEBUG -> debug(message, context);
            case INFO -> info(message, context);
            case NOTICE -> notice(message, context);
            case WARNING -> warning(message, context);
            case ERROR -> error(message, context);
            case CRITICAL -> critical(message, context);
            case EMERGENCY -> emergency(message, context);
        }
    }
}
