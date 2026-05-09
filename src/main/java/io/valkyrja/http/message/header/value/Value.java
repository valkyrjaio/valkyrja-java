/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.value;

import io.valkyrja.http.message.header.factory.HeaderFactory;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetSetException;
import io.valkyrja.http.message.header.throwable.exception.HttpHeaderUnsupportedOffsetUnsetException;
import io.valkyrja.http.message.header.value.component.contract.ComponentContract;
import io.valkyrja.http.message.header.value.contract.ValueContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Value implements ValueContract, Iterable<ComponentContract> {

    protected List<Object> components = new ArrayList<>();

    protected int position = 0;

    public Value(Object... components) {
        this.components = filterComponents(components);
    }

    public static ValueContract fromValue(String value) {
        String[] parts;

        String delimiter = ";";

        if (value.contains(delimiter)) {
            parts = value.split(delimiter, -1);
        } else {
            parts = new String[]{value};
        }

        return new Value((Object[]) parts);
    }

    @Override
    public List<Object> getComponents() {
        return components;
    }

    @Override
    public ValueContract withComponents(Object... components) {
        Value newValue = copy();
        newValue.components = filterComponents(components);
        return newValue;
    }

    @Override
    public ValueContract withAddedComponents(Object... components) {
        Value newValue = (Value) withComponents(components);
        List<Object> merged = new ArrayList<>(this.components);
        merged.addAll(newValue.components);
        newValue.components = merged;
        return newValue;
    }

    @Override
    public String jsonSerialize() {
        return toString();
    }

    @Override
    public String toString() {
        return components.stream()
            .map(c -> c instanceof ComponentContract ? c.toString() : (String) c)
            .filter(s -> !s.isEmpty())
            .map(String::trim)
            .collect(Collectors.joining("; "));
    }

    public boolean offsetExists(int offset) {
        return offset >= 0 && offset < components.size();
    }

    public Object offsetGet(int offset) {
        return components.get(offset);
    }

    public void offsetSet(int offset, Object value) {
        throw new HttpHeaderUnsupportedOffsetSetException("Use withValues or withAddedValues");
    }

    public void offsetUnset(int offset) {
        throw new HttpHeaderUnsupportedOffsetUnsetException("Use withValues or withAddedValues");
    }

    public int count() {
        return components.size();
    }

    public Object current() {
        return components.get(position);
    }

    public void next() {
        position++;
    }

    public int key() {
        return position;
    }

    public boolean valid() {
        return position >= 0 && position < components.size();
    }

    public void rewind() {
        position = 0;
    }

    @Override
    public Iterator<ComponentContract> iterator() {
        return components.stream()
            .filter(c -> c instanceof ComponentContract)
            .map(c -> (ComponentContract) c)
            .iterator();
    }

    protected Value copy() {
        Value newValue = new Value();
        newValue.components = new ArrayList<>(this.components);
        newValue.position = this.position;
        return newValue;
    }

    protected List<Object> filterComponents(Object... components) {
        List<Object> filtered = new ArrayList<>();

        for (Object component : components) {
            if (component instanceof String s) {
                s = HeaderFactory.filterValue(s);
                HeaderFactory.assertValidValue(s);
                filtered.add(s);
            } else {
                filtered.add(component);
            }
        }

        return filtered;
    }
}