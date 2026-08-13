/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.command;

import io.valkyrja.application.constant.ApplicationInfo;
import io.valkyrja.cli.interaction.message.Header;
import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.NewLine;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.routing.data.contract.OptionParameterContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import org.jspecify.annotations.Nullable;

public class VersionCommand {

    protected String appNamespace;
    protected String appVersion;
    protected RouteContract route;
    protected OutputFactoryContract outputFactory;

    public VersionCommand(
            OutputFactoryContract outputFactory,
            String appNamespace,
            String appVersion,
            RouteContract route) {
        this.outputFactory = outputFactory;
        this.appNamespace = appNamespace;
        this.appVersion = appVersion;
        this.route = route;
    }

    public static MessageContract help() {
        return new Message("A command to show the application version and info.");
    }

    public OutputContract run() {
        if (spelledOption("short") != null) {
            return outputFactory.createOutput().withMessages(new Message(appVersion));
        }

        if (spelledOption("plain") != null) {
            return outputFactory
                    .createOutput()
                    .withMessages(
                            new Message(appNamespace + " v" + appVersion),
                            new NewLine(),
                            new Message(
                                    "Built on Valkyrja v"
                                            + ApplicationInfo.VERSION
                                            + " (date: "
                                            + ApplicationInfo.VERSION_BUILD_DATE_TIME
                                            + ")"),
                            new NewLine(),
                            new Message("Running on Java " + System.getProperty("java.version")));
        }

        return outputFactory
                .createOutput()
                .withMessages(new Header(appNamespace, appVersion, route));
    }

    /** Returns the declared option where the input spelled it, and null where it did not. */
    protected @Nullable OptionParameterContract spelledOption(String name) {
        if (!route.hasOption(name)) {
            return null;
        }

        OptionParameterContract option = route.getOption(name);

        return option.hasFirstValue() ? option : null;
    }
}
