/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
