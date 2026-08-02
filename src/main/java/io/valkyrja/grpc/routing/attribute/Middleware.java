/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
