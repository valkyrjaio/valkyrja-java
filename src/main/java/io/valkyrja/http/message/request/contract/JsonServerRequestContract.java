/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.contract;

import io.valkyrja.http.message.param.contract.ParsedJsonParamCollectionContract;

public interface JsonServerRequestContract extends ServerRequestContract {

    ParsedJsonParamCollectionContract getParsedJson();

    JsonServerRequestContract withParsedJson(ParsedJsonParamCollectionContract params);
}
