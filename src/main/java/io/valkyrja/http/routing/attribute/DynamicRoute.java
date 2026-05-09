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

    String[] routeMatchedMiddleware() default {};

    String[] routeDispatchedMiddleware() default {};

    String[] throwableCaughtMiddleware() default {};

    String[] sendingResponseMiddleware() default {};

    String[] terminatedMiddleware() default {};

    Class<?> requestStruct() default Object.class;

    Class<?> responseStruct() default Object.class;
}