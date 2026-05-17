/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.value.contract;

import java.util.List;

/**
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7230#section-3.2">RFC 7230 Section 3.2</a>
 */
public interface ValueContract {

    static ValueContract fromValue(String value) {
        throw new UnsupportedOperationException(
                "fromValue must be implemented by the concrete class");
    }

    List<Object> getComponents();

    ValueContract withComponents(Object... components);

    ValueContract withAddedComponents(Object... components);

    String jsonSerialize();

    String toString();
}
