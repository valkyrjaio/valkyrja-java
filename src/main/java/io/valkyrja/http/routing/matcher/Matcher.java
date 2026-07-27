/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.matcher;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.matcher.contract.MatcherContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;
import io.valkyrja.type.data.Cast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.jspecify.annotations.Nullable;

public class Matcher implements MatcherContract {

    protected RouteCollectionContract collection;

    public Matcher() {
        this(new RouteCollection());
    }

    public Matcher(RouteCollectionContract collection) {
        this.collection = collection;
    }

    @Override
    public @Nullable RouteContract match(String path, RequestMethod requestMethod) {
        path = "/" + path.replaceAll("^/+|/+$", "");
        RouteContract route = matchStatic(path, requestMethod);
        return route != null ? route : matchDynamic(path, requestMethod);
    }

    @Override
    public @Nullable RouteContract matchStatic(String path, RequestMethod requestMethod) {
        if (collection.hasPath(path, requestMethod)) {
            return collection.getByPath(path, requestMethod);
        }
        return null;
    }

    @Override
    public @Nullable RouteContract matchDynamic(String path, RequestMethod requestMethod) {
        Map<String, String> regexMap = collection.getRegexes(requestMethod);

        for (Map.Entry<String, String> entry : regexMap.entrySet()) {
            String regex = entry.getKey();
            if (regex.isEmpty()) continue;

            try {
                java.util.regex.Matcher matcher = Pattern.compile(regex).matcher(path);
                if (matcher.matches()) {
                    DynamicRouteContract dynamicRoute = collection.getByRegex(regex, requestMethod);
                    return processArguments(dynamicRoute, matcher);
                }
            } catch (PatternSyntaxException ignored) {
            }
        }

        return null;
    }

    protected DynamicRouteContract processArguments(
            DynamicRouteContract route, java.util.regex.Matcher matcher) {
        List<ParameterContract> parameters = route.getParameters();

        if (parameters.isEmpty()) {
            throw new HttpRoutingInvalidRoutePathException("Route parameters must not be empty");
        }

        List<ParameterContract> parametersWithValues = new ArrayList<>();

        for (ParameterContract parameter : parameters) {
            String name = parameter.getName();
            String match;
            try {
                match = matcher.group(name);
            } catch (IllegalArgumentException e) {
                match = null;
            }

            if (match == null) {
                match = (String) parameter.getDefault();
            }

            if (match == null) {
                parametersWithValues.add(parameter);
                continue;
            }

            Object value = checkAndCastMatchValue(parameter, match);
            parametersWithValues.add(parameter.withValue(value));
        }

        return route.withParameters(parametersWithValues.toArray(new ParameterContract[0]));
    }

    protected Object checkAndCastMatchValue(ParameterContract parameter, String match) {
        if (parameter.hasCast()) {
            return castMatchValue(parameter.getCast(), match);
        }
        return match;
    }

    protected Object castMatchValue(Cast cast, String match) {
        return match;
    }
}
