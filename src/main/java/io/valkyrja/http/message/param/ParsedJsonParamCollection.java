/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.param;

import io.valkyrja.http.message.param.abstract_.ParamCollection;
import io.valkyrja.http.message.param.contract.ParsedJsonParamCollectionContract;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class ParsedJsonParamCollection extends ParamCollection
        implements ParsedJsonParamCollectionContract {

    public ParsedJsonParamCollection(Map<String, Object> params) {
        super(params);
    }

    public static ParsedJsonParamCollection fromArray(Map<String, Object> data) {
        return (ParsedJsonParamCollection) fromArrayInternal(data, ParsedJsonParamCollection::new);
    }

    @Override
    public @Nullable Object get(String key) {
        return params.getOrDefault(key, null);
    }

    @Override
    protected ParsedJsonParamCollection copy() {
        return new ParsedJsonParamCollection(params);
    }
}
