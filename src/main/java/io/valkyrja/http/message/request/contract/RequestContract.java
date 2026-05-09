/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.contract;

import io.valkyrja.http.message.contract.MessageContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.uri.contract.UriContract;

public interface RequestContract extends MessageContract {

    String getRequestTarget();

    RequestContract withRequestTarget(String requestTarget);

    RequestMethod getMethod();

    RequestContract withMethod(RequestMethod method);

    UriContract getUri();

    RequestContract withUri(UriContract uri, boolean preserveHost);
}
