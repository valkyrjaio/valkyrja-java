/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.throwable.handler.abstract_;

import io.valkyrja.throwable.exception.RuntimeException;
import io.valkyrja.throwable.handler.contract.ThrowableHandlerContract;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

public abstract class ThrowableHandler implements ThrowableHandlerContract {

    /**
     * Get a trace code for a throwable using MD5 over the class name and stack trace.
     *
     * @param throwable the throwable
     * @return an MD5 hex trace code
     */
    public static String getTraceCode(Throwable throwable) {
        String className = throwable.getClass().getName();
        String traceAsString = Arrays.toString(throwable.getStackTrace());
        String input = className + traceAsString;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            // MD5 is always available in Java
            throw new RuntimeException("MD5 algorithm not available", exception);
        }
    }
}
