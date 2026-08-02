/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.struct;

import io.valkyrja.http.struct.response.abstract_.ResponseStruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Concrete response struct mapping internal keys to output names. */
public final class ResponseStructFixture extends ResponseStruct {

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
