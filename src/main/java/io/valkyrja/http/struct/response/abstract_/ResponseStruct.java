/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
