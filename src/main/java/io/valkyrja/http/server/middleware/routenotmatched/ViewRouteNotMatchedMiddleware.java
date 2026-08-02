/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.server.middleware.routenotmatched;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import java.util.Map;
import java.util.function.BiFunction;

public class ViewRouteNotMatchedMiddleware implements RouteNotMatchedMiddlewareContract {

    protected String errorsTemplateDir = "errors";
    protected BiFunction<String, Map<String, Object>, String> renderer;

    public ViewRouteNotMatchedMiddleware(BiFunction<String, Map<String, Object>, String> renderer) {
        this.renderer = renderer;
    }

    @Override
    public ResponseContract routeNotMatched(
            ServerRequestContract request,
            ResponseContract response,
            RouteNotMatchedHandlerContract handler) {
        String name = errorsTemplateDir + "/" + response.getStatusCode().getValue();
        Map<String, Object> variables = Map.of("request", request, "response", response);

        String view = renderer.apply(name, variables);

        Stream stream = new Stream();
        stream.write(view);
        stream.rewind();

        return (ResponseContract) response.withBody(stream);
    }
}
