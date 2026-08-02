/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.param;

import io.valkyrja.http.message.param.abstract_.ParamCollection;
import io.valkyrja.http.message.param.contract.AttributeParamCollectionContract;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class AttributeParamCollection extends ParamCollection
        implements AttributeParamCollectionContract {

    public AttributeParamCollection(Map<String, Object> params) {
        super(params);
    }

    public AttributeParamCollection() {
        super(Map.of());
    }

    public static AttributeParamCollection fromArray(Map<String, Object> data) {
        return (AttributeParamCollection) fromArrayInternal(data, AttributeParamCollection::new);
    }

    @Override
    public @Nullable Object get(String key) {
        return params.get(key);
    }

    @Override
    protected AttributeParamCollection copy() {
        return new AttributeParamCollection(params);
    }
}
