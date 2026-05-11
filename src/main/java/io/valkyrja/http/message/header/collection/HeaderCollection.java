/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.collection;

import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidHeaderNameException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderInvalidHeaderParamException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HeaderCollection implements HeaderCollectionContract {

    protected LinkedHashMap<String, HeaderContract> headers = new LinkedHashMap<>();

    protected int position = 0;

    public HeaderCollection(HeaderContract... headers) {
        setHeaders(headers);
    }

    public static HeaderCollection fromArray(Map<?, ?> data) {
        HeaderContract[] headers = new HeaderContract[data.size()];
        int i = 0;

        for (Map.Entry<?, ?> entry : data.entrySet()) {
            validateHeader(entry.getValue());
            headers[i++] = (HeaderContract) entry.getValue();
        }

        return new HeaderCollection(headers);
    }

    protected static void validateHeader(Object param) {
        if (!(param instanceof HeaderContract)) {
            throw new HttpHeaderInvalidHeaderParamException("Param must be header");
        }
    }

    @Override
    public boolean has(String name) {
        return headers.containsKey(name.toLowerCase());
    }

    @Override
    public HeaderContract get(String name) {
        HeaderContract header = headers.get(name.toLowerCase());

        if (header == null) {
            throw new HttpHeaderInvalidHeaderNameException("Header " + name + " does not exist");
        }

        return header;
    }

    @Override
    public String getHeaderLine(String name) {
        if (!has(name)) {
            return "";
        }

        return get(name).getHeaderLine();
    }

    @Override
    public Map<String, HeaderContract> getAll() {
        return headers;
    }

    @Override
    public Map<String, HeaderContract> getOnly(String... names) {
        Set<String> nameSet = Arrays.stream(names).collect(Collectors.toSet());

        return headers.entrySet().stream()
            .filter(entry -> nameSet.contains(entry.getKey()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    @Override
    public Map<String, HeaderContract> getAllExcept(String... names) {
        Set<String> nameSet = Arrays.stream(names).collect(Collectors.toSet());

        return headers.entrySet().stream()
            .filter(entry -> !nameSet.contains(entry.getKey()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    @Override
    public HeaderCollectionContract withHeader(HeaderContract header) {
        HeaderCollection newCollection = copy();
        newCollection.overrideHeader(header);
        return newCollection;
    }

    @Override
    public HeaderCollectionContract withoutHeader(String name) {
        if (!has(name)) {
            return copy();
        }

        String headerName = name.toLowerCase();
        HeaderCollection newCollection = copy();
        newCollection.headers.remove(headerName);
        return newCollection;
    }

    @Override
    public HeaderCollectionContract withHeaders(HeaderContract... headers) {
        HeaderCollection newCollection = copy();
        newCollection.setHeaders(headers);
        return newCollection;
    }

    @Override
    public HeaderCollectionContract withAddedHeaders(HeaderContract... headers) {
        HeaderCollection newCollection = copy();

        for (HeaderContract header : headers) {
            newCollection.addHeader(header);
        }

        return newCollection;
    }

    @Override
    public HeaderCollectionContract withoutHeaders(String... names) {
        HeaderCollection newCollection = copy();

        for (String name : names) {
            newCollection.removeHeader(name);
        }

        return newCollection;
    }

    protected void removeHeader(String name) {
        if (!has(name)) {
            return;
        }

        headers.remove(name.toLowerCase());
    }

    protected void overrideHeader(HeaderContract header) {
        String name = header.getNormalizedName();
        headers.put(name, header);
    }

    protected void addHeader(HeaderContract header) {
        String name = header.getNormalizedName();

        if (!has(name)) {
            headers.put(name, header);
            return;
        }

        HeaderContract existingHeader = get(name);
        headers.put(name, existingHeader.withAddedValues(header.getValues().toArray()));
    }

    protected void setHeaders(HeaderContract... originalHeaders) {
        LinkedHashMap<String, HeaderContract> newHeaders = new LinkedHashMap<>();

        for (HeaderContract header : originalHeaders) {
            String headerName = header.getNormalizedName();
            newHeaders.put(headerName, header);
        }

        this.headers = newHeaders;
    }

    protected HeaderCollection copy() {
        HeaderCollection newCollection = new HeaderCollection();
        newCollection.headers = new LinkedHashMap<>(this.headers);
        newCollection.position = this.position;
        return newCollection;
    }
}
