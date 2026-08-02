/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.param;

import io.valkyrja.http.message.param.abstract_.ParamCollection;
import io.valkyrja.http.message.param.contract.ServerParamCollectionContract;
import java.util.Map;

public class ServerParamCollection extends ParamCollection
        implements ServerParamCollectionContract {

    public ServerParamCollection(Map<String, Object> params) {
        super(params);
    }

    public static ServerParamCollection fromArray(Map<String, Object> data) {
        return (ServerParamCollection) fromArrayInternal(data, ServerParamCollection::new);
    }

    @Override
    public Object get(String key) {
        return params.getOrDefault(key, "");
    }

    @Override
    protected ServerParamCollection copy() {
        return new ServerParamCollection(params);
    }
}
