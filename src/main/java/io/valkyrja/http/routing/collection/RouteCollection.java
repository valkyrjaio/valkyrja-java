/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.collection;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.data.HttpRoutingData;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidDynamicRouteNameException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteNameException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteRegexException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class RouteCollection implements RouteCollectionContract {

    protected Map<String, Supplier<RouteContract>> routes = new LinkedHashMap<>();
    protected Map<String, Map<String, String>> paths = new LinkedHashMap<>();
    protected Map<String, Map<String, String>> dynamicPaths = new LinkedHashMap<>();
    protected Map<String, Map<String, String>> regexes = new LinkedHashMap<>();

    @Override
    public HttpRoutingData getData() {
        return new HttpRoutingData(routes, paths, dynamicPaths, regexes);
    }

    @Override
    public void setFromData(HttpRoutingData data) {
        this.routes = new LinkedHashMap<>(data.routes());
        this.paths = new LinkedHashMap<>(data.paths());
        this.dynamicPaths = new LinkedHashMap<>(data.dynamicPaths());
        this.regexes = new LinkedHashMap<>(data.regexes());
    }

    @Override
    public void add(RouteContract route) {
        setRouteToRequestMethods(route);
        String name = route.getName();
        routes.put(name, () -> route);
    }

    @Override
    public boolean hasPath(String path, RequestMethod method) {
        if (method != RequestMethod.ANY) {
            String type = method.getValue();
            return (paths.containsKey(type) && paths.get(type).containsKey(path))
                    || (dynamicPaths.containsKey(type) && dynamicPaths.get(type).containsKey(path));
        }
        return RequestMethod.all().stream().anyMatch(m -> hasPath(path, m));
    }

    @Override
    public RouteContract getByPath(String path, RequestMethod method) {
        RouteContract route = internalGetByPath(path, method);
        if (route != null) {
            return route;
        }
        throw new HttpRoutingInvalidRoutePathException(
                "The path '"
                        + path
                        + "' is not a valid route for the given method '"
                        + method.getValue()
                        + "'");
    }

    @Override
    public boolean hasRegex(String regex, RequestMethod method) {
        if (method != RequestMethod.ANY) {
            String type = method.getValue();
            return regexes.containsKey(type) && regexes.get(type).containsKey(regex);
        }
        return RequestMethod.all().stream().anyMatch(m -> hasRegex(regex, m));
    }

    @Override
    public DynamicRouteContract getByRegex(String regex, RequestMethod method) {
        DynamicRouteContract route = internalGetByRegex(regex, method);
        if (route != null) {
            return route;
        }
        throw new HttpRoutingInvalidRouteRegexException(
                "The regex '"
                        + regex
                        + "' is not a valid route for the given method '"
                        + method.getValue()
                        + "'");
    }

    @Override
    public Map<String, String> getPaths(RequestMethod method) {
        if (method != RequestMethod.ANY) {
            return paths.getOrDefault(method.getValue(), Map.of());
        }
        Map<String, String> all = new LinkedHashMap<>();
        for (RequestMethod m : RequestMethod.all()) {
            all.putAll(paths.getOrDefault(m.getValue(), Map.of()));
            all.putAll(dynamicPaths.getOrDefault(m.getValue(), Map.of()));
        }
        return all;
    }

    @Override
    public Map<String, String> getRegexes(RequestMethod method) {
        if (method != RequestMethod.ANY) {
            return regexes.getOrDefault(method.getValue(), Map.of());
        }
        Map<String, String> all = new LinkedHashMap<>();
        for (RequestMethod m : RequestMethod.all()) {
            all.putAll(regexes.getOrDefault(m.getValue(), Map.of()));
        }
        return all;
    }

    @Override
    public boolean hasName(String name) {
        return routes.containsKey(name);
    }

    @Override
    public RouteContract getByName(String name) {
        Supplier<RouteContract> supplier = routes.get(name);
        if (supplier != null) {
            return supplier.get();
        }
        throw new HttpRoutingInvalidRouteNameException(
                "A route with the name '" + name + "' does not exist");
    }

    @Override
    public Map<String, RouteContract> getAll(RequestMethod method) {
        Map<String, String> pathMap = getPaths(method);
        Map<String, RouteContract> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            result.put(entry.getKey(), getRouteFromName(entry.getValue()));
        }
        return result;
    }

    protected @Nullable RouteContract internalGetByPath(String path, RequestMethod method) {
        if (method != RequestMethod.ANY) {
            String type = method.getValue();
            String name = null;
            if (paths.containsKey(type)) {
                name = paths.get(type).get(path);
            }
            if (name == null && dynamicPaths.containsKey(type)) {
                name = dynamicPaths.get(type).get(path);
            }
            return name != null ? getRouteFromName(name) : null;
        }
        for (RequestMethod m :
                List.of(
                        RequestMethod.GET,
                        RequestMethod.HEAD,
                        RequestMethod.POST,
                        RequestMethod.PATCH,
                        RequestMethod.PUT,
                        RequestMethod.DELETE,
                        RequestMethod.OPTIONS,
                        RequestMethod.TRACE,
                        RequestMethod.CONNECT)) {
            RouteContract route = internalGetByPath(path, m);
            if (route != null) return route;
        }
        return null;
    }

    protected @Nullable DynamicRouteContract internalGetByRegex(
            String regex, RequestMethod method) {
        if (method != RequestMethod.ANY) {
            String type = method.getValue();
            if (regexes.containsKey(type)) {
                String name = regexes.get(type).get(regex);
                if (name != null) {
                    return getDynamicRouteFromName(name);
                }
            }
            return null;
        }
        for (RequestMethod m :
                List.of(
                        RequestMethod.GET,
                        RequestMethod.HEAD,
                        RequestMethod.POST,
                        RequestMethod.PATCH,
                        RequestMethod.PUT,
                        RequestMethod.DELETE,
                        RequestMethod.OPTIONS,
                        RequestMethod.TRACE,
                        RequestMethod.CONNECT)) {
            DynamicRouteContract route = internalGetByRegex(regex, m);
            if (route != null) return route;
        }
        return null;
    }

    protected void setRouteToRequestMethods(RouteContract route) {
        List<RequestMethod> requestMethods = route.getRequestMethods();
        boolean hasAny = requestMethods.contains(RequestMethod.ANY);
        List<RequestMethod> methods = hasAny ? RequestMethod.all() : requestMethods;
        for (RequestMethod method : methods) {
            setRouteToRequestMethod(route, method);
        }
    }

    protected void setRouteToRequestMethod(RouteContract route, RequestMethod method) {
        if (method == RequestMethod.ANY) {
            return;
        }
        String name = route.getName();
        String path = route.getPath();
        String type = method.getValue();
        if (route instanceof DynamicRouteContract dynamic) {
            String regex = dynamic.getRegex();
            dynamicPaths.computeIfAbsent(type, k -> new LinkedHashMap<>()).put(path, name);
            regexes.computeIfAbsent(type, k -> new LinkedHashMap<>()).put(regex, name);
        } else {
            paths.computeIfAbsent(type, k -> new LinkedHashMap<>()).put(path, name);
        }
    }

    protected RouteContract getRouteFromName(String name) {
        Supplier<RouteContract> supplier = routes.get(name);
        if (supplier == null) {
            throw new HttpRoutingInvalidRouteNameException("Invalid name `" + name + "` provided");
        }
        return supplier.get();
    }

    protected DynamicRouteContract getDynamicRouteFromName(String name) {
        RouteContract route = getRouteFromName(name);
        if (route instanceof DynamicRouteContract dynamic) {
            return dynamic;
        }
        throw new HttpRoutingInvalidDynamicRouteNameException("Invalid dynamic route " + name);
    }
}
