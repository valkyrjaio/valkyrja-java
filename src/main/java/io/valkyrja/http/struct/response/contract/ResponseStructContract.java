/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.struct.response.contract;

import io.valkyrja.http.struct.contract.StructContract;

import java.util.Map;

public interface ResponseStructContract extends StructContract {

    Map<String, Object> getStructuredData(Map<String, Object> data, boolean includeAll);
}
