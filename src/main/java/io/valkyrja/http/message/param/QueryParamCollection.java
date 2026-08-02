/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.param;

import io.valkyrja.http.message.param.abstract_.ParamCollection;
import io.valkyrja.http.message.param.contract.QueryParamCollectionContract;
import java.util.Map;

public class QueryParamCollection extends ParamCollection implements QueryParamCollectionContract {

    public QueryParamCollection(Map<String, Object> params) {
        super(params);
    }

    public static QueryParamCollection fromArray(Map<String, Object> data) {
        return (QueryParamCollection) fromArrayInternal(data, QueryParamCollection::new);
    }

    @Override
    public Object get(String key) {
        return params.getOrDefault(key, "");
    }

    @Override
    protected QueryParamCollection copy() {
        return new QueryParamCollection(params);
    }
}
