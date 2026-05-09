/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.param;

import io.valkyrja.http.message.param.abstract_.ParamCollection;
import io.valkyrja.http.message.param.contract.AttributeParamCollectionContract;

import java.util.Map;

public class AttributeParamCollection extends ParamCollection implements AttributeParamCollectionContract {

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
    public Object get(String key) {
        return params.get(key);
    }

    @Override
    protected AttributeParamCollection copy() {
        return new AttributeParamCollection(params);
    }
}