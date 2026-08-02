/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
