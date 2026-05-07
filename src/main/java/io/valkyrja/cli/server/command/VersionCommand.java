/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.server.command;

import io.valkyrja.application.constant.ApplicationInfo;
import io.valkyrja.cli.interaction.message.Header;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;

public class VersionCommand {

    protected String appNamespace;
    protected String appVersion;
    protected RouteContract route;
    protected OutputFactoryContract outputFactory;

    public VersionCommand(OutputFactoryContract outputFactory, String appNamespace, String appVersion, RouteContract route) {
        this.outputFactory = outputFactory;
        this.appNamespace = appNamespace;
        this.appVersion = appVersion;
        this.route = route;
    }

    public static MessageContract help() {
        return new Message("A command to show the application version and info.");
    }

    public OutputContract run() {
        if (route.hasOption("short")) {
            return outputFactory.createOutput().withMessages(new Message(appVersion));
        }

        if (route.hasOption("plain")) {
            return outputFactory.createOutput().withMessages(
                new Message(appNamespace + " v" + appVersion),
                new NewLine(),
                new Message("Built on Valkyrja v" + ApplicationInfo.VERSION + " (date: " + ApplicationInfo.VERSION_BUILD_DATE_TIME + ")"),
                new NewLine(),
                new Message("Running on Java " + System.getProperty("java.version"))
            );
        }

        return outputFactory.createOutput().withMessages(new Header(appNamespace, appVersion, route));
    }
}