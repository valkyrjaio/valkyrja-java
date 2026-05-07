/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.throwable.handler.contract;

/** Contract for Valkyrja throwable handlers. */
public interface ThrowableHandlerContract {

    /**
     * Get a trace code for a throwable.
     *
     * @param throwable the throwable
     * @return a stable trace code string
     */
    static String getTraceCode(Throwable throwable) {
        throw new UnsupportedOperationException("Implement in subclass");
    }
}
