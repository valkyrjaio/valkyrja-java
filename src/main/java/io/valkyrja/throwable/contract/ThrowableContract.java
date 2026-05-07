/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.throwable.contract;

/**
 * Contract for all Valkyrja throwables.
 *
 * <p>Extends the standard Java {@link Throwable} interface to add framework-specific functionality
 * such as a stable trace code that uniquely identifies an exception type.
 */
public interface ThrowableContract {

    /**
     * Get a stable trace code unique to this throwable's type and stack trace.
     *
     * @return an MD5-based trace code string
     */
    String getTraceCode();
}
