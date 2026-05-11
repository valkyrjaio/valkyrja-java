/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.struct.response.abstract_;

import io.valkyrja.http.struct.response.contract.ResponseStructContract;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ResponseStruct implements ResponseStructContract {

    @Override
    public Map<String, Object> getStructuredData(Map<String, Object> data, boolean includeAll) {
        Map<String, String> schema = asMap();
        Map<String, Object> structured = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : schema.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!includeAll && !data.containsKey(key)) {
                continue;
            }

            structured.put(value, data.getOrDefault(key, null));
        }

        return structured;
    }
}
