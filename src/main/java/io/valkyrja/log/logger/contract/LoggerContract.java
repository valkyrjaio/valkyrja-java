/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.log.logger.contract;

import io.valkyrja.log.enum_.LogLevel;
import java.util.Map;

public interface LoggerContract {

    void debug(String message, Map<String, Object> context);

    void info(String message, Map<String, Object> context);

    void notice(String message, Map<String, Object> context);

    void warning(String message, Map<String, Object> context);

    void error(String message, Map<String, Object> context);

    void critical(String message, Map<String, Object> context);

    void alert(String message, Map<String, Object> context);

    void emergency(String message, Map<String, Object> context);

    void log(LogLevel level, String message, Map<String, Object> context);

    void throwable(Throwable throwable, String message, Map<String, Object> context);
}
