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
import io.valkyrja.http.message.header.ContentType;
import org.junit.jupiter.api.Test;

/** Test the {@link ContentType}. */
final class ContentTypeTest {

    @Test
    void usesContentTypeNameAndValue() {
        var header = new ContentType("text/html");

        assertEquals(HeaderName.CONTENT_TYPE, header.getName());
        assertTrue(header.getHeaderLine().contains("text/html"));
    }
}
