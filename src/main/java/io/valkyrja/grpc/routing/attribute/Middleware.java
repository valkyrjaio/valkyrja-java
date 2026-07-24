/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.attribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Attaches a middleware to a {@link Method} route. The collector dispatches the class to the
 * matching stage by its middleware contract type, so one annotation serves every stage.
 */
@Target(ElementType.METHOD)
@Repeatable(Middlewares.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {

    /** The middleware class, implementing one of the stage middleware contracts. */
    Class<?> name();
}
