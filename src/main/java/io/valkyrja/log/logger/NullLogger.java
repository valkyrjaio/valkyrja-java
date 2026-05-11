/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.log.logger;

import io.valkyrja.log.logger.abstract_.Logger;

import java.util.Map;

public class NullLogger extends Logger {

    @Override
    public void debug(String message, Map<String, Object> context) {
    }

    @Override
    public void info(String message, Map<String, Object> context) {
    }

    @Override
    public void notice(String message, Map<String, Object> context) {
    }

    @Override
    public void warning(String message, Map<String, Object> context) {
    }

    @Override
    public void error(String message, Map<String, Object> context) {
    }

    @Override
    public void critical(String message, Map<String, Object> context) {
    }

    @Override
    public void alert(String message, Map<String, Object> context) {
    }

    @Override
    public void emergency(String message, Map<String, Object> context) {
    }

    @Override
    public void throwable(Throwable throwable, String message, Map<String, Object> context) {
    }
}
