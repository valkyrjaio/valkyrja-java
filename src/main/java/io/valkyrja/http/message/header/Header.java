/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header;

import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.factory.HeaderFactory;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetSetException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetUnsetException;
import io.valkyrja.http.message.header.value.contract.ValueContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Header implements HeaderContract, Iterable<Object> {

    protected String name;
    protected String normalizedName;
    protected List<Object> values = new ArrayList<>();
    protected int position = 0;

    public Header(String name, Object... values) {
        rewind();
        updateName(name);
        updateValues(values);
    }

    public static HeaderContract fromValue(String value) {
        String header = value;
        String valuesAsString = "";
        String[] valuesArr;

        String delimiter = ":";

        if (value.contains(delimiter)) {
            int idx = value.indexOf(delimiter);
            header = value.substring(0, idx);
            valuesAsString = value.substring(idx + 1);
            valuesArr = new String[]{valuesAsString};
        } else {
            valuesArr = new String[0];
        }

        String valueDelimiter = ",";

        if (valuesAsString.contains(valueDelimiter)) {
            valuesArr = valuesAsString.split(valueDelimiter, -1);
        }

        return new Header(header, (Object[]) valuesArr);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNormalizedName() {
        return normalizedName;
    }

    @Override
    public HeaderContract withName(String name) {
        Header newHeader = copy();
        newHeader.updateName(name);
        return newHeader;
    }

    @Override
    public List<Object> getValues() {
        return values;
    }

    @Override
    public HeaderContract withValues(Object... values) {
        Header newHeader = copy();
        newHeader.updateValues(values);
        return newHeader;
    }

    @Override
    public HeaderContract withAddedValues(Object... values) {
        Header newHeader = (Header) withValues(values);
        List<Object> merged = new ArrayList<>(this.values);
        merged.addAll(newHeader.values);
        newHeader.values = merged;
        return newHeader;
    }

    @Override
    public String getHeaderLine() {
        return valuesToString();
    }

    @Override
    public String jsonSerialize() {
        return toString();
    }

    @Override
    public String toString() {
        String valuesStr = valuesToString();

        if (valuesStr.isEmpty()) {
            return "";
        }

        return nameToString() + valuesStr;
    }

    @Override
    public boolean offsetExists(int offset) {
        return offset >= 0 && offset < values.size();
    }

    @Override
    public Object offsetGet(int offset) {
        return values.get(offset);
    }

    @Override
    public void offsetSet(int offset, Object value) {
        throw new HttpHeaderUnsupportedOffsetSetException("Use withValues or withAddedValues");
    }

    @Override
    public void offsetUnset(int offset) {
        throw new HttpHeaderUnsupportedOffsetUnsetException("Use withValues or withAddedValues");
    }

    @Override
    public int count() {
        return values.size();
    }

    @Override
    public Object current() {
        return values.get(position);
    }

    @Override
    public void next() {
        position++;
    }

    @Override
    public int key() {
        return position;
    }

    @Override
    public boolean valid() {
        return position >= 0 && position < values.size();
    }

    @Override
    public void rewind() {
        position = 0;
    }

    @Override
    public Iterator<Object> iterator() {
        return values.iterator();
    }

    protected void updateName(String name) {
        HeaderFactory.assertValidName(name);
        this.name = name;
        this.normalizedName = name.toLowerCase();
    }

    protected void updateValues(Object... values) {
        this.values = filterValues(values);
    }

    protected String nameToString() {
        return name + ": ";
    }

    protected String valuesToString() {
        return values.stream()
            .map(v -> v instanceof ValueContract vc ? vc.toString() : (String) v)
            .filter(s -> !s.isEmpty())
            .map(String::trim)
            .collect(Collectors.joining(", "));
    }

    protected List<Object> filterValues(Object... values) {
        List<Object> filtered = new ArrayList<>();

        for (Object value : values) {
            if (value instanceof String s) {
                s = HeaderFactory.filterValue(s);
                HeaderFactory.assertValidValue(s);
                filtered.add(s);
            } else {
                filtered.add(value);
            }
        }

        return filtered;
    }

    protected Header copy() {
        Header newHeader = new Header(this.name);
        newHeader.values = new ArrayList<>(this.values);
        newHeader.position = this.position;
        return newHeader;
    }
}
