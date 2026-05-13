/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.middleware.handler.abstract_;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.middleware.handler.contract.HandlerContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Handler<M> implements HandlerContract<M> {

    protected ContainerContract container;
    protected List<Class<? extends M>> middleware = new ArrayList<>();
    protected Class<? extends M> next = null;
    protected int index = 0;

    @SafeVarargs
    public Handler(ContainerContract container, Class<? extends M>... middleware) {
        this.container = container;
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    @Override
    @SafeVarargs
    public final void add(Class<? extends M>... middleware) {
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    @SuppressWarnings("unchecked")
    protected M getMiddleware(Class<? extends M> className) {
        M instance = (M) container.getSingleton(className);
        index++;
        updateNext();
        return instance;
    }

    protected void updateNext() {
        next = index < middleware.size() ? middleware.get(index) : null;
    }
}
