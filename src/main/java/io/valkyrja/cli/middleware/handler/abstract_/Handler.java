/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.middleware.handler.abstract_;

import io.valkyrja.cli.middleware.handler.contract.HandlerContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;

public abstract class Handler<M> implements HandlerContract<M> {

    protected final ContainerContract container;
    protected final List<Class<? extends M>> middleware = new ArrayList<>();
    protected @Nullable Class<? extends M> next = null;
    protected int index = 0;

    @SafeVarargs
    protected Handler(ContainerContract container, Class<? extends M>... middleware) {
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
        M item = (M) container.getSingleton(className);
        index++;
        updateNext();
        return item;
    }

    protected void updateNext() {
        next = index < middleware.size() ? middleware.get(index) : null;
    }
}
