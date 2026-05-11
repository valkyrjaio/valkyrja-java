/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.header.value.component;

import io.valkyrja.http.message.header.factory.HeaderFactory;
import io.valkyrja.http.message.header.value.component.contract.ComponentContract;

public class Component implements ComponentContract {

    protected String token;
    protected String text;

    public Component(String token) {
        this(token, "");
    }

    public Component(String token, String text) {
        this.token = filterPart(token);
        this.text = filterText(text);
    }

    public static ComponentContract fromValue(String value) {
        String token = value;
        String text = "";

        String delimiter = "=";

        if (value.contains(delimiter)) {
            int idx = value.indexOf(delimiter);
            token = value.substring(0, idx);
            text = value.substring(idx + 1);
        }

        return new Component(token.trim(), text.trim());
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public ComponentContract withToken(String token) {
        Component newComponent = new Component(this.token, this.text);
        newComponent.token = filterPart(token);
        return newComponent;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public ComponentContract withText(String text) {
        Component newComponent = new Component(this.token, this.text);
        newComponent.text = filterText(text);
        return newComponent;
    }

    @Override
    public String jsonSerialize() {
        return toString();
    }

    @Override
    public String toString() {
        return !token.isEmpty() && !text.isEmpty()
            ? token + "=" + text
            : token;
    }

    protected String filterText(String text) {
        if (text.isEmpty()) {
            return "";
        }

        return filterPart(text);
    }

    protected String filterPart(String part) {
        part = HeaderFactory.filterValue(part);

        HeaderFactory.assertValidValue(part);

        return part;
    }
}
