/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data.contract;

import java.util.List;

public interface DynamicRouteContract extends RouteContract {

    String getRegex();

    DynamicRouteContract withRegex(String regex);

    List<ParameterContract> getParameters();

    DynamicRouteContract withParameters(ParameterContract... parameters);

    DynamicRouteContract withAddedParameters(ParameterContract... parameters);

    ParameterContract getParameter(String name);

    boolean hasParameter(String name);
}