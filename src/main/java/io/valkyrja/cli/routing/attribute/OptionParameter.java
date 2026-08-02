/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.routing.attribute;

import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Repeatable(OptionParameters.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionParameter {

    String name();

    String description();

    String valueDisplayName() default "";

    String defaultValue() default "";

    String[] shortNames() default {};

    String[] validValues() default {};

    OptionMode mode() default OptionMode.OPTIONAL;

    OptionValueMode valueMode() default OptionValueMode.DEFAULT;
}
