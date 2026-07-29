/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.interaction.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.cli.interaction.data.CliInteractionConfig;
import io.valkyrja.cli.interaction.data.contract.CliInteractionConfigContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.interaction.provider.CliInteractionServiceProvider;
import io.valkyrja.container.manager.Container;
import org.junit.jupiter.api.Test;

/** Test the {@link CliInteractionServiceProvider}. */
final class CliInteractionServiceProviderTest {

    @Test
    void publishersExposesConfigAndOutputFactory() {
        var publishers = new CliInteractionServiceProvider().publishers();

        assertEquals(2, publishers.size());
    }

    @Test
    void publishConfigReusesConfigThatIsAlreadyInteractionConfig() {
        var container = new Container();
        var config =
                mock(
                        ConfigContract.class,
                        withSettings().extraInterfaces(CliInteractionConfigContract.class));
        container.setSingleton(ConfigContract.class, config);

        CliInteractionServiceProvider.publishConfig(container);

        assertSame(config, container.getSingleton(CliInteractionConfigContract.class));
    }

    @Test
    void publishConfigCreatesDefaultWhenConfigIsNotInteractionConfig() {
        var container = new Container();
        container.setSingleton(ConfigContract.class, mock(ConfigContract.class));

        CliInteractionServiceProvider.publishConfig(container);

        assertInstanceOf(
                CliInteractionConfig.class,
                container.getSingleton(CliInteractionConfigContract.class));
    }

    @Test
    void publishOutputFactoryBindsOutputFactory() {
        var container = new Container();
        container.setSingleton(CliInteractionConfigContract.class, new CliInteractionConfig());

        CliInteractionServiceProvider.publishOutputFactory(container);

        assertInstanceOf(
                OutputFactoryContract.class, container.getSingleton(OutputFactoryContract.class));
    }
}
