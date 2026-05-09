/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.collection.contract;

import io.valkyrja.http.message.header.contract.HeaderContract;

import java.util.Map;

public interface HeaderCollectionContract {

    boolean has(String name);

    HeaderContract get(String name);

    String getHeaderLine(String name);

    Map<String, HeaderContract> getAll();

    Map<String, HeaderContract> getOnly(String... names);

    Map<String, HeaderContract> getAllExcept(String... names);

    HeaderCollectionContract withHeader(HeaderContract header);

    HeaderCollectionContract withoutHeader(String name);

    HeaderCollectionContract withHeaders(HeaderContract... headers);

    HeaderCollectionContract withAddedHeaders(HeaderContract... headers);

    HeaderCollectionContract withoutHeaders(String... names);
}