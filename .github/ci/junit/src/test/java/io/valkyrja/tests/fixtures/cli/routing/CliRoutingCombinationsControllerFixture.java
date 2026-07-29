/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.fixtures.cli.routing;

import io.valkyrja.cli.routing.attribute.ArgumentParameter;
import io.valkyrja.cli.routing.attribute.OptionParameter;
import io.valkyrja.cli.routing.attribute.Route;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;

/**
 * Annotated command exercising a matrix of argument and option modes/value-modes so the annotation
 * construction path can be asserted to convert every permutation into the expected data-class
 * parameters.
 */
@io.valkyrja.cli.routing.attribute.route.Name("combinations")
public final class CliRoutingCombinationsControllerFixture {

    @Route(name = "run", description = "Run permutations")
    @ArgumentParameter(
            name = "required",
            description = "A required single-value argument",
            mode = ArgumentMode.REQUIRED,
            valueMode = ArgumentValueMode.DEFAULT)
    @ArgumentParameter(
            name = "rest",
            description = "An optional array argument",
            mode = ArgumentMode.OPTIONAL,
            valueMode = ArgumentValueMode.ARRAY)
    @OptionParameter(
            name = "format",
            description = "A required single-value option",
            valueDisplayName = "fmt",
            defaultValue = "json",
            shortNames = {"f"},
            validValues = {"json", "xml"},
            mode = OptionMode.REQUIRED,
            valueMode = OptionValueMode.DEFAULT)
    @OptionParameter(
            name = "flag",
            description = "A valueless flag option",
            mode = OptionMode.OPTIONAL,
            valueMode = OptionValueMode.NONE)
    @OptionParameter(
            name = "tags",
            description = "A repeatable array option",
            valueMode = OptionValueMode.ARRAY)
    public void run() {}
}
