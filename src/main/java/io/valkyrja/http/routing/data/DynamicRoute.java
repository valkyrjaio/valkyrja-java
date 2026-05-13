/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRouteParameterException;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class DynamicRoute extends Route implements DynamicRouteContract {

    protected String regex;
    protected List<ParameterContract> parameters;

    public DynamicRoute(
            String path,
            String name,
            String regex,
            List<ParameterContract> parameters,
            BiFunction<ContainerContract, RouteContract, ResponseContract> handler,
            List<RequestMethod> requestMethods,
            List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware,
            List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware,
            List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware,
            List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware,
            List<Class<? extends TerminatedMiddlewareContract>> terminatedMiddleware,
            RequestStructContract requestStruct,
            ResponseStructContract responseStruct
    ) {
        super(path, name, handler, requestMethods, routeMatchedMiddleware, routeDispatchedMiddleware,
                throwableCaughtMiddleware, sendingResponseMiddleware, terminatedMiddleware, requestStruct, responseStruct);
        this.regex = regex;
        this.parameters = new ArrayList<>(parameters);
    }

    public DynamicRoute(String path, String name, String regex, List<ParameterContract> parameters,
            BiFunction<ContainerContract, RouteContract, ResponseContract> handler) {
        this(path, name, regex, parameters, handler, List.of(RequestMethod.HEAD, RequestMethod.GET),
                List.of(), List.of(), List.of(), List.of(), List.of(), null, null);
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public DynamicRouteContract withRegex(String regex) {
        DynamicRoute copy = (DynamicRoute) copy();
        copy.regex = regex;
        return copy;
    }

    @Override
    public List<ParameterContract> getParameters() {
        return List.copyOf(parameters);
    }

    @Override
    public DynamicRouteContract withParameters(ParameterContract... parameters) {
        DynamicRoute copy = (DynamicRoute) copy();
        copy.parameters = new ArrayList<>(Arrays.asList(parameters));
        return copy;
    }

    @Override
    public DynamicRouteContract withAddedParameters(ParameterContract... parameters) {
        DynamicRoute copy = (DynamicRoute) copy();
        copy.parameters.addAll(Arrays.asList(parameters));
        return copy;
    }

    @Override
    public ParameterContract getParameter(String name) {
        return parameters.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new HttpRoutingInvalidRouteParameterException("No parameter named '" + name + "' exists on this route"));
    }

    @Override
    public boolean hasParameter(String name) {
        return parameters.stream().anyMatch(p -> p.getName().equals(name));
    }

    @Override
    protected DynamicRoute copy() {
        return new DynamicRoute(path, name, regex, parameters, handler, requestMethods, routeMatchedMiddleware,
                routeDispatchedMiddleware, throwableCaughtMiddleware, sendingResponseMiddleware, terminatedMiddleware,
                requestStruct, responseStruct);
    }
}
