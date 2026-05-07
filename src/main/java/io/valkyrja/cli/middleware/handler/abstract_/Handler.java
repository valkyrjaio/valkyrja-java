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

public abstract class Handler implements HandlerContract {

    protected final ContainerContract container;
    protected final List<Class<?>> middleware = new ArrayList<>();
    protected Class<?> next = null;
    protected int index = 0;

    protected Handler(ContainerContract container) {
        this.container = container;
    }

    @Override
    public void add(Class<?>... middleware) {
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    protected Object getMiddleware(Class<?> middlewareClass) {
        Object item = container.get(middlewareClass);
        index++;
        updateNext();
        return item;
    }

    protected void updateNext() {
        next = index < middleware.size() ? middleware.get(index) : null;
    }
}
