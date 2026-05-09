/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.handler.abstract_;

import io.valkyrja.http.middleware.handler.contract.HandlerContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Handler<M> implements HandlerContract<M> {

    protected List<String> middleware = new ArrayList<>();
    protected String next = null;
    protected int index = 0;

    @SafeVarargs
    public Handler(String... middleware) {
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    @Override
    public void add(String... middleware) {
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    @SuppressWarnings("unchecked")
    protected M getMiddleware(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            M instance = (M) clazz.getDeclaredConstructor().newInstance();
            index++;
            updateNext();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate middleware: " + className, e);
        }
    }

    protected void updateNext() {
        next = index < middleware.size() ? middleware.get(index) : null;
    }
}