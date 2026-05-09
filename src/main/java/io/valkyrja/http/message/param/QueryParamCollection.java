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