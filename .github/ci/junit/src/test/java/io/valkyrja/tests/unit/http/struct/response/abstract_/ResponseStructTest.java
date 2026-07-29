/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.struct.response.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.tests.fixtures.http.struct.ResponseStructClass;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the abstract {@code ResponseStruct} via a concrete fixture. */
final class ResponseStructTest {

    @Test
    void mapsKeysToOutputNames() {
        var struct = new ResponseStructClass();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 7);
        data.put("name", "bob");

        var structured = struct.getStructuredData(data, false);

        assertEquals(7, structured.get("identifier"));
        assertEquals("bob", structured.get("full_name"));
    }

    @Test
    void skipsMissingKeysUnlessIncludeAll() {
        var struct = new ResponseStructClass();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 7);

        // includeAll = false drops the absent "name" mapping.
        assertFalse(struct.getStructuredData(data, false).containsKey("full_name"));
        // includeAll = true emits it with a null value.
        var all = struct.getStructuredData(data, true);
        assertTrue(all.containsKey("full_name"));
        assertEquals(null, all.get("full_name"));
    }
}
