/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.attribute;

import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Repeatable(ArgumentParameters.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgumentParameter {

    String name();

    String description();

    ArgumentMode mode() default ArgumentMode.OPTIONAL;

    ArgumentValueMode valueMode() default ArgumentValueMode.DEFAULT;
}
