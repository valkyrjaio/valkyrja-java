/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.param;

import io.valkyrja.http.message.param.abstract_.ParamCollection;
import io.valkyrja.http.message.param.contract.CookieParamCollectionContract;
import java.util.Map;

public class CookieParamCollection extends ParamCollection
        implements CookieParamCollectionContract {

    public CookieParamCollection(Map<String, Object> params) {
        super(params);
    }

    public static CookieParamCollection fromArray(Map<String, Object> data) {
        return (CookieParamCollection) fromArrayInternal(data, CookieParamCollection::new);
    }

    @Override
    public String get(String key) {
        Object value = params.getOrDefault(key, "");
        return value instanceof String ? (String) value : (value != null ? value.toString() : "");
    }

    @Override
    protected CookieParamCollection copy() {
        return new CookieParamCollection(params);
    }
}
