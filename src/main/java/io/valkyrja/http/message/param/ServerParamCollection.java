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
import io.valkyrja.http.message.param.contract.ServerParamCollectionContract;

import java.util.Map;

public class ServerParamCollection extends ParamCollection implements ServerParamCollectionContract {

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