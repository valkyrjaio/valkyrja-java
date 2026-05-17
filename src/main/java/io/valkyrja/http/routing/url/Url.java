/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.url;

import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteNameException;
import io.valkyrja.http.routing.url.contract.UrlContract;
import java.util.Map;

public class Url implements UrlContract {

    protected RouteCollectionContract collection;

    public Url(RouteCollectionContract collection) {
        this.collection = collection;
    }

    @Override
    public String getUrl(String name, Map<String, Object> data)
            throws HttpRoutingInvalidRouteNameException {
        String path = collection.getByName(name).getPath();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            path = path.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        return path;
    }
}
