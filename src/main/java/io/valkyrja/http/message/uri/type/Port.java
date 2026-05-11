/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.type;

import io.valkyrja.http.message.uri.throwable.exception.HttpUriInvalidPortException;

public class Port {

    private final int subject;

    public Port(int subject) {
        if (subject >= 1 && subject <= 65535) {
            this.subject = subject;
            return;
        }

        throw new HttpUriInvalidPortException("Invalid port argument passed.");
    }

    public static Port fromValue(Object value) {
        if (!(value instanceof Integer)) {
            throw new HttpUriInvalidPortException(
                "Int expected value of type `" + (value == null ? "null" : value.getClass().getSimpleName()) + "` provided"
            );
        }

        return new Port((Integer) value);
    }

    public int asFlatValue() {
        return subject;
    }
}
