/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.header.Location;
import org.junit.jupiter.api.Test;

/** Test the {@link Location}. */
final class LocationTest {

    @Test
    void usesLocationName() {
        var header = new Location("/home");

        assertEquals(HeaderName.LOCATION, header.getName());
        assertTrue(header.getHeaderLine().contains("/home"));
    }
}
