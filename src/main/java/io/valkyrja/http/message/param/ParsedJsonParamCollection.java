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
import io.valkyrja.http.message.param.contract.ParsedJsonParamCollectionContract;

import java.util.Map;

public class ParsedJsonParamCollection extends ParamCollection implements ParsedJsonParamCollectionContract {

    public ParsedJsonParamCollection(Map<String, Object> params) {
        super(params);
    }

    public static ParsedJsonParamCollection fromArray(Map<String, Object> data) {
        return (ParsedJsonParamCollection) fromArrayInternal(data, ParsedJsonParamCollection::new);
    }

    @Override
    public Object get(String key) {
        return params.getOrDefault(key, null);
    }

    @Override
    protected ParsedJsonParamCollection copy() {
        return new ParsedJsonParamCollection(params);
    }
}
