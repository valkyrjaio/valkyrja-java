/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.collector;

import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.routing.attribute.DynamicRoute;
import io.valkyrja.http.routing.attribute.DynamicRoutes;
import io.valkyrja.http.routing.attribute.Parameter;
import io.valkyrja.http.routing.attribute.Parameters;
import io.valkyrja.http.routing.attribute.Route;
import io.valkyrja.http.routing.attribute.Routes;
import io.valkyrja.http.routing.attribute.route.Middleware;
import io.valkyrja.http.routing.attribute.route.Middlewares;
import io.valkyrja.http.routing.attribute.route.Name;
import io.valkyrja.http.routing.attribute.route.Path;
import io.valkyrja.http.routing.attribute.route.RequestMethod;
import io.valkyrja.http.routing.attribute.route.RouteHandler;
import io.valkyrja.http.routing.collector.contract.RouteCollectorContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.processor.Processor;
import io.valkyrja.http.routing.processor.contract.ProcessorContract;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class AttributeRouteCollector implements RouteCollectorContract {

    protected ProcessorContract processor;

    public AttributeRouteCollector() {
        this(new Processor());
    }

    public AttributeRouteCollector(ProcessorContract processor) {
        this.processor = processor;
    }

    @Override
    public List<RouteContract> getRoutes(Class<?>... classes) {
        List<RouteContract> routes = new ArrayList<>();

        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                routes.addAll(collectRoutesFromMethod(clazz, method));
                routes.addAll(collectDynamicRoutesFromMethod(clazz, method));
            }
        }

        return routes;
    }

    protected List<RouteContract> collectRoutesFromMethod(Class<?> clazz, Method method) {
        List<RouteContract> routes = new ArrayList<>();

        List<Route> annotations = new ArrayList<>();
        if (method.isAnnotationPresent(Routes.class)) {
            annotations.addAll(Arrays.asList(method.getAnnotation(Routes.class).value()));
        } else if (method.isAnnotationPresent(Route.class)) {
            annotations.add(method.getAnnotation(Route.class));
        }

        for (Route annotation : annotations) {
            io.valkyrja.http.routing.data.Route route = buildRouteFromAnnotation(annotation, clazz, method);
            route = (io.valkyrja.http.routing.data.Route) updatePath(route, clazz, method);
            route = (io.valkyrja.http.routing.data.Route) updateName(route, clazz, method);
            route = (io.valkyrja.http.routing.data.Route) updateMiddleware(route, clazz, method);
            route = (io.valkyrja.http.routing.data.Route) updateRequestMethods(route, clazz, method);
            routes.add(processor.route(route));
        }

        return routes;
    }

    protected List<RouteContract> collectDynamicRoutesFromMethod(Class<?> clazz, Method method) {
        List<RouteContract> routes = new ArrayList<>();

        List<DynamicRoute> annotations = new ArrayList<>();
        if (method.isAnnotationPresent(DynamicRoutes.class)) {
            annotations.addAll(Arrays.asList(method.getAnnotation(DynamicRoutes.class).value()));
        } else if (method.isAnnotationPresent(DynamicRoute.class)) {
            annotations.add(method.getAnnotation(DynamicRoute.class));
        }

        for (DynamicRoute annotation : annotations) {
            io.valkyrja.http.routing.data.DynamicRoute route = buildDynamicRouteFromAnnotation(annotation, clazz, method);
            route = (io.valkyrja.http.routing.data.DynamicRoute) updatePath(route, clazz, method);
            route = (io.valkyrja.http.routing.data.DynamicRoute) updateName(route, clazz, method);
            route = (io.valkyrja.http.routing.data.DynamicRoute) updateMiddleware(route, clazz, method);
            route = (io.valkyrja.http.routing.data.DynamicRoute) updateRequestMethods(route, clazz, method);
            route = updateParameters(route, clazz, method);
            routes.add(processor.route(route));
        }

        return routes;
    }

    protected io.valkyrja.http.routing.data.Route buildRouteFromAnnotation(Route annotation, Class<?> clazz, Method method) {
        return new io.valkyrja.http.routing.data.Route(
            annotation.path(),
            annotation.name(),
            buildHandler(clazz, method),
            Arrays.asList(annotation.requestMethods()),
            Arrays.asList(annotation.routeMatchedMiddleware()),
            Arrays.asList(annotation.routeDispatchedMiddleware()),
            Arrays.asList(annotation.throwableCaughtMiddleware()),
            Arrays.asList(annotation.sendingResponseMiddleware()),
            Arrays.asList(annotation.terminatedMiddleware()),
            buildRequestStruct(annotation.requestStruct()),
            buildResponseStruct(annotation.responseStruct())
        );
    }

    protected io.valkyrja.http.routing.data.DynamicRoute buildDynamicRouteFromAnnotation(DynamicRoute annotation, Class<?> clazz, Method method) {
        List<io.valkyrja.http.routing.data.Parameter> parameters = new ArrayList<>();
        for (Parameter param : annotation.parameters()) {
            parameters.add(new io.valkyrja.http.routing.data.Parameter(param.name(), param.regex()));
        }

        return new io.valkyrja.http.routing.data.DynamicRoute(
            annotation.path(),
            annotation.name(),
            "",
            new ArrayList<>(parameters),
            buildHandler(clazz, method),
            Arrays.asList(annotation.requestMethods()),
            Arrays.asList(annotation.routeMatchedMiddleware()),
            Arrays.asList(annotation.routeDispatchedMiddleware()),
            Arrays.asList(annotation.throwableCaughtMiddleware()),
            Arrays.asList(annotation.sendingResponseMiddleware()),
            Arrays.asList(annotation.terminatedMiddleware()),
            buildRequestStruct(annotation.requestStruct()),
            buildResponseStruct(annotation.responseStruct())
        );
    }

    protected BiFunction<io.valkyrja.container.manager.contract.ContainerContract, RouteContract, io.valkyrja.http.message.response.contract.ResponseContract> buildHandler(Class<?> clazz, Method method) {
        return (container, route) -> {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                return (io.valkyrja.http.message.response.contract.ResponseContract) method.invoke(instance, container, route);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke route handler: " + clazz.getName() + "#" + method.getName(), e);
            }
        };
    }

    protected RequestStructContract buildRequestStruct(Class<? extends RequestStructContract> structClass) {
        if (structClass == RequestStructContract.class) return null;
        try {
            return structClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    protected ResponseStructContract buildResponseStruct(Class<? extends ResponseStructContract> structClass) {
        if (structClass == ResponseStructContract.class) return null;
        try {
            return structClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    protected RouteContract updatePath(RouteContract route, Class<?> clazz, Method method) {
        Path classPath = clazz.getAnnotation(Path.class);
        if (classPath != null) {
            route = route.withPath(classPath.value() + route.getPath());
        }

        Path methodPath = method.getAnnotation(Path.class);
        if (methodPath != null) {
            route = route.withAddedPath(methodPath.value());
        }

        return route;
    }

    protected RouteContract updateName(RouteContract route, Class<?> clazz, Method method) {
        Name className = clazz.getAnnotation(Name.class);
        if (className != null) {
            route = route.withName(className.value() + "." + route.getName());
        }

        Name methodName = method.getAnnotation(Name.class);
        if (methodName != null) {
            route = route.withName(route.getName() + "." + methodName.value());
        }

        return route;
    }

    @SuppressWarnings("unchecked")
    protected RouteContract updateMiddleware(RouteContract route, Class<?> clazz, Method method) {
        List<Middleware> middlewareList = new ArrayList<>();
        if (method.isAnnotationPresent(Middlewares.class)) {
            middlewareList.addAll(Arrays.asList(method.getAnnotation(Middlewares.class).value()));
        } else if (method.isAnnotationPresent(Middleware.class)) {
            middlewareList.add(method.getAnnotation(Middleware.class));
        }

        for (Middleware mw : middlewareList) {
            Class<?> middlewareClass = mw.name();

            if (RouteMatchedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route = route.withAddedRouteMatchedMiddleware((Class<? extends RouteMatchedMiddlewareContract>) middlewareClass);
            }
            if (RouteDispatchedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route = route.withAddedRouteDispatchedMiddleware((Class<? extends RouteDispatchedMiddlewareContract>) middlewareClass);
            }
            if (ThrowableCaughtMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route = route.withAddedThrowableCaughtMiddleware((Class<? extends ThrowableCaughtMiddlewareContract>) middlewareClass);
            }
            if (SendingResponseMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route = route.withAddedSendingResponseMiddleware((Class<? extends SendingResponseMiddlewareContract>) middlewareClass);
            }
            if (TerminatedMiddlewareContract.class.isAssignableFrom(middlewareClass)) {
                route = route.withAddedTerminatedMiddleware((Class<? extends TerminatedMiddlewareContract>) middlewareClass);
            }
        }

        return route;
    }

    protected RouteContract updateRequestMethods(RouteContract route, Class<?> clazz, Method method) {
        RequestMethod requestMethodAnnotation = method.getAnnotation(RequestMethod.class);
        if (requestMethodAnnotation != null) {
            route = route.withAddedRequestMethods(requestMethodAnnotation.requestMethods());
        }
        return route;
    }

    protected io.valkyrja.http.routing.data.DynamicRoute updateParameters(io.valkyrja.http.routing.data.DynamicRoute route, Class<?> clazz, Method method) {
        List<io.valkyrja.http.routing.data.Parameter> parameters = new ArrayList<>();
        for (io.valkyrja.http.routing.data.contract.ParameterContract p : route.getParameters()) {
            parameters.add(convertToDataParameter(p));
        }

        List<Parameter> methodParams = new ArrayList<>();
        if (method.isAnnotationPresent(Parameters.class)) {
            methodParams.addAll(Arrays.asList(method.getAnnotation(Parameters.class).value()));
        } else if (method.isAnnotationPresent(Parameter.class)) {
            methodParams.add(method.getAnnotation(Parameter.class));
        }

        for (Parameter param : methodParams) {
            parameters.add(new io.valkyrja.http.routing.data.Parameter(param.name(), param.regex()));
        }

        return (io.valkyrja.http.routing.data.DynamicRoute) route.withParameters(
            parameters.toArray(new io.valkyrja.http.routing.data.contract.ParameterContract[0])
        );
    }

    protected io.valkyrja.http.routing.data.Parameter convertToDataParameter(io.valkyrja.http.routing.data.contract.ParameterContract parameter) {
        return new io.valkyrja.http.routing.data.Parameter(
            parameter.getName(),
            parameter.getRegex(),
            parameter.hasCast() ? parameter.getCast() : null,
            parameter.isOptional(),
            parameter.shouldCapture(),
            parameter.getDefault(),
            null
        );
    }
}
