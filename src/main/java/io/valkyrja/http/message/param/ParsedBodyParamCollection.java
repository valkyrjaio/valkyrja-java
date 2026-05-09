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
import io.valkyrja.http.message.param.contract.ParsedBodyParamCollectionContract;

import java.util.Map;

public class ParsedBodyParamCollection extends ParamCollection implements ParsedBodyParamCollectionContract {

    public ParsedBodyParamCollection(Map<String, Object> params) {
        super(params);
    }

    public static ParsedBodyParamCollection fromArray(Map<String, Object> data) {
        return (ParsedBodyParamCollection) fromArrayInternal(data, ParsedBodyParamCollection::new);
    }

    @Override
    public Object get(String key) {
        return params.getOrDefault(key, "");
    }

    @Override
    protected ParsedBodyParamCollection copy() {
        return new ParsedBodyParamCollection(params);
    }
}