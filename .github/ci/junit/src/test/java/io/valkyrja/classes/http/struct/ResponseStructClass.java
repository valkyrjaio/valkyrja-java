/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.http.struct;

import io.valkyrja.http.struct.response.abstract_.ResponseStruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Concrete response struct mapping internal keys to output names. */
public final class ResponseStructClass extends ResponseStruct {

    @Override
    public Map<String, String> asMap() {
        var map = new LinkedHashMap<String, String>();
        map.put("id", "identifier");
        map.put("name", "full_name");
        return map;
    }

    @Override
    public List<String> values() {
        return List.of("id", "name");
    }
}
