/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.collection.contract;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.routing.data.HttpRoutingData;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

import java.util.Map;

public interface RouteCollectionContract {

    HttpRoutingData getData();

    void setFromData(HttpRoutingData data);

    void add(RouteContract route);

    boolean hasPath(String path, RequestMethod method);

    RouteContract getByPath(String path, RequestMethod method);

    boolean hasRegex(String regex, RequestMethod method);

    DynamicRouteContract getByRegex(String regex, RequestMethod method);

    Map<String, String> getPaths(RequestMethod method);

    Map<String, String> getRegexes(RequestMethod method);

    boolean hasName(String name);

    RouteContract getByName(String name);

    Map<String, RouteContract> getAll(RequestMethod method);
}
