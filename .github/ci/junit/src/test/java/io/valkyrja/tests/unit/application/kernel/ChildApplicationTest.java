/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.application.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.application.data.Config;
import io.valkyrja.application.kernel.ChildApplication;
import io.valkyrja.application.kernel.Valkyrja;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.NativeChildContainer;
import io.valkyrja.tests.fixtures.container.SingletonFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link ChildApplication}: own container, all other methods delegate to parent. */
final class ChildApplicationTest {

    private Valkyrja parent;
    private ChildApplication child;
    private NativeChildContainer childContainer;

    private static Config configWith(List<Consumer<ApplicationContract>> callbacks) {
        return new Config(
                "App",
                System.getProperty("user.dir"),
                "1.0.0",
                "production",
                false,
                "UTC",
                "secret_app_key",
                "app/provider/data",
                "app.provider.data",
                List.of(),
                callbacks);
    }

    @BeforeEach
    void setUp() {
        var config = new Config();
        var parentContainer = new Container();
        parent = new Valkyrja(parentContainer, config);

        childContainer = new NativeChildContainer(parentContainer);
        child = new ChildApplication(parent, childContainer);
    }

    // getContainer

    @Test
    void getContainerReturnsChildContainer() {
        assertSame(childContainer, child.getContainer());
        assertNotSame(parent.getContainer(), child.getContainer());
    }

    // publishProviderCallbacks — delegates to parent, parent app is passed

    @Test
    void publishProviderCallbacksDelegatesToParent() {
        var received = new ArrayList<ApplicationContract>();

        List<Consumer<ApplicationContract>> callbacks = List.of(received::add);
        var parentContainer = new Container();
        var localParent = new Valkyrja(parentContainer, configWith(callbacks));
        var localChild =
                new ChildApplication(localParent, new NativeChildContainer(parentContainer));

        localChild.publishProviderCallbacks();

        // The callback must be invoked with the parent application, not the child.
        assertEquals(1, received.size());
        assertSame(localParent, received.get(0));
        assertNotSame(localChild, received.get(0));
    }

    // Delegation — all non-container methods return the parent's values

    @Test
    void getProvidersDelegatesToParent() {
        assertSame(parent.getProviders(), child.getProviders());
    }

    @Test
    void getContainerProvidersDelegatesToParent() {
        assertEquals(parent.getContainerProviders(), child.getContainerProviders());
    }

    @Test
    void getEventProvidersDelegatesToParent() {
        assertEquals(parent.getEventProviders(), child.getEventProviders());
    }

    @Test
    void getCliProvidersDelegatesToParent() {
        assertEquals(parent.getCliProviders(), child.getCliProviders());
    }

    @Test
    void getHttpProvidersDelegatesToParent() {
        assertEquals(parent.getHttpProviders(), child.getHttpProviders());
    }

    @Test
    void getGrpcProvidersDelegatesToParent() {
        assertEquals(parent.getGrpcProviders(), child.getGrpcProviders());
    }

    @Test
    void getDebugModeDelegatesToParent() {
        assertEquals(parent.getDebugMode(), child.getDebugMode());
    }

    @Test
    void getEnvironmentDelegatesToParent() {
        assertEquals(parent.getEnvironment(), child.getEnvironment());
    }

    @Test
    void getVersionDelegatesToParent() {
        assertEquals(parent.getVersion(), child.getVersion());
    }

    // Container isolation — child writes must not reach the parent container

    @Test
    void childContainerWriteDoesNotAffectParentContainer() {
        var instance = new SingletonFixture();
        child.getContainer().setSingleton(SingletonFixture.class, instance);

        assertFalse(parent.getContainer().isSingletonInstance(SingletonFixture.class));
    }

    @Test
    void childContainerServesItsOwnRegistrations() {
        var instance = new SingletonFixture();
        child.getContainer().setSingleton(SingletonFixture.class, instance);

        assertSame(instance, child.getContainer().getSingleton(SingletonFixture.class));
    }

    // Alternative container type — ChildContainer (portable default)

    @Test
    void worksWithChildContainer() {
        var parentContainer = new Container();
        var localParent = new Valkyrja(parentContainer, new Config());
        var data = (ContainerData) parentContainer.getData();
        var localChildContainer = new ChildContainer(parentContainer, data);
        var localChild = new ChildApplication(localParent, localChildContainer);

        assertSame(localChildContainer, localChild.getContainer());
        assertEquals(localParent.getEnvironment(), localChild.getEnvironment());
        assertEquals(localParent.getVersion(), localChild.getVersion());
    }

    // Multiple children — independent containers, same parent delegation

    @Test
    void multipleChildrenHaveIndependentContainers() {
        var child2Container = new NativeChildContainer((Container) parent.getContainer());
        var child2 = new ChildApplication(parent, child2Container);

        assertNotSame(child.getContainer(), child2.getContainer());
        assertEquals(child.getEnvironment(), child2.getEnvironment());
    }

    @Test
    void multipleChildrenContainerWritesAreIsolatedFromEachOther() {
        var child2Container = new NativeChildContainer((Container) parent.getContainer());
        var child2 = new ChildApplication(parent, child2Container);

        var instance = new SingletonFixture();
        child.getContainer().setSingleton(SingletonFixture.class, instance);

        assertFalse(child2.getContainer().isSingletonInstance(SingletonFixture.class));
    }
}
