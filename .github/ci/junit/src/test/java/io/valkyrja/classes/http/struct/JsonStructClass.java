/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.http.struct;

import io.valkyrja.http.struct.request.abstract_.JsonRequestStruct;
import java.util.List;
import java.util.Map;

/** Concrete JSON request struct selecting the name field. */
public final class JsonStructClass extends JsonRequestStruct {

    @Override
    public Map<String, String> asMap() {
        return Map.of("name", "name");
    }

    @Override
    public List<String> values() {
        return List.of("name");
    }
}
