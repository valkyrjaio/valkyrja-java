/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.attribute;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.TerminatedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.struct.request.contract.RequestStructContract;
import io.valkyrja.http.struct.response.contract.ResponseStructContract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Repeatable(DynamicRoutes.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicRoute {

    String path();

    String name();

    Parameter[] parameters() default {};

    RequestMethod[] requestMethods() default {RequestMethod.HEAD, RequestMethod.GET};

    Class<? extends RouteMatchedMiddlewareContract>[] routeMatchedMiddleware() default {};

    Class<? extends RouteDispatchedMiddlewareContract>[] routeDispatchedMiddleware() default {};

    Class<? extends ThrowableCaughtMiddlewareContract>[] throwableCaughtMiddleware() default {};

    Class<? extends SendingResponseMiddlewareContract>[] sendingResponseMiddleware() default {};

    Class<? extends TerminatedMiddlewareContract>[] terminatedMiddleware() default {};

    Class<? extends RequestStructContract> requestStruct() default RequestStructContract.class;

    Class<? extends ResponseStructContract> responseStruct() default ResponseStructContract.class;
}
