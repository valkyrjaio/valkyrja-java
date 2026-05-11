/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.struct.request.abstract_;

import io.valkyrja.http.message.request.contract.ServerRequestContract;

import java.util.Map;

public abstract class QueryRequestStruct extends RequestStruct {

    @Override
    protected Map<String, Object> getOnlyParamsFromRequest(ServerRequestContract request, String... values) {
        return request.getQueryParams().getOnly(values);
    }

    @Override
    protected Map<String, Object> getExceptParamsFromRequest(ServerRequestContract request, String... values) {
        return request.getQueryParams().getAllExcept(values);
    }
}
