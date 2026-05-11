/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.matcher.contract;

import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.routing.data.contract.RouteContract;

public interface MatcherContract {

    RouteContract match(String path, RequestMethod requestMethod);

    RouteContract matchStatic(String path, RequestMethod requestMethod);

    RouteContract matchDynamic(String path, RequestMethod requestMethod);
}
