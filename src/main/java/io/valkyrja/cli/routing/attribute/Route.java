/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.attribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Repeatable(Routes.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

    String name();

    String description();

    String[] routeMatchedMiddleware() default {};

    String[] routeDispatchedMiddleware() default {};

    String[] throwableCaughtMiddleware() default {};

    String[] exitedMiddleware() default {};
}
