/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.interaction.message;

import io.valkyrja.application.constant.ApplicationInfo;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Header extends Message {

    protected String appName;
    protected String appVersion;
    protected String icon;
    protected String valkyrjaVersion;
    protected String valkyrjaBuildDate;
    protected String javaVersion;
    protected String projectRoot;
    protected String actionDescription;
    protected String commandName;

    public Header(String appName, String appVersion, RouteContract route) {
        this(appName, appVersion, route,
            ApplicationInfo.ASCII,
            ApplicationInfo.VERSION,
            ApplicationInfo.VERSION_BUILD_DATE_TIME,
            System.getProperty("java.version"),
            "",
            "",
            "");
    }

    public Header(
            String appName,
            String appVersion,
            RouteContract route,
            String icon,
            String valkyrjaVersion,
            String valkyrjaBuildDate,
            String javaVersion,
            String projectRoot,
            String actionDescription,
            String commandName) {
        super("");
        this.appName = appName;
        this.appVersion = appVersion;
        this.icon = icon;
        this.valkyrjaVersion = valkyrjaVersion;
        this.valkyrjaBuildDate = valkyrjaBuildDate;
        this.javaVersion = javaVersion;
        this.projectRoot = projectRoot.isEmpty() ? Paths.get("").toAbsolutePath().toString() : projectRoot;
        this.actionDescription = actionDescription.isEmpty() ? route.getDescription() : actionDescription;
        this.commandName = commandName.isEmpty() ? route.getName() : commandName;
    }

    public Header withAppName(String appName) {
        Header copy = shallowCopy();
        copy.appName = appName;
        return copy;
    }

    public Header withAppVersion(String appVersion) {
        Header copy = shallowCopy();
        copy.appVersion = appVersion;
        return copy;
    }

    public Header withIcon(String icon) {
        Header copy = shallowCopy();
        copy.icon = icon;
        return copy;
    }

    public Header withValkyrjaVersion(String valkyrjaVersion) {
        Header copy = shallowCopy();
        copy.valkyrjaVersion = valkyrjaVersion;
        return copy;
    }

    public Header withValkyrjaBuildDate(String valkyrjaBuildDate) {
        Header copy = shallowCopy();
        copy.valkyrjaBuildDate = valkyrjaBuildDate;
        return copy;
    }

    public Header withJavaVersion(String javaVersion) {
        Header copy = shallowCopy();
        copy.javaVersion = javaVersion;
        return copy;
    }

    public Header withProjectRoot(String projectRoot) {
        Header copy = shallowCopy();
        copy.projectRoot = projectRoot;
        return copy;
    }

    public Header withActionDescription(String actionDescription) {
        Header copy = shallowCopy();
        copy.actionDescription = actionDescription;
        return copy;
    }

    public Header withCommandName(String commandName) {
        Header copy = shallowCopy();
        copy.commandName = commandName;
        return copy;
    }

    @Override
    public String getText() {
        String[] iconLines = Arrays.stream(icon.split("\n"))
            .map(line -> "│   " + line)
            .toArray(String[]::new);

        String[] lines = new String[]{
            "╭── " + appName + " v" + appVersion,
            "│",
        };
        lines = concat(lines, iconLines);
        lines = concat(lines, new String[]{
            "│",
            "│   Built on Valkyrja v" + valkyrjaVersion + " (date: " + valkyrjaBuildDate + ")",
            "│   Running on Java " + javaVersion,
            "│   " + projectRoot,
            "╰── " + actionDescription + " · " + commandName,
        });

        return Arrays.stream(lines).collect(Collectors.joining("\n"));
    }

    private Header shallowCopy() {
        Header copy = new Header("");
        copy.appName = appName;
        copy.appVersion = appVersion;
        copy.icon = icon;
        copy.valkyrjaVersion = valkyrjaVersion;
        copy.valkyrjaBuildDate = valkyrjaBuildDate;
        copy.javaVersion = javaVersion;
        copy.projectRoot = projectRoot;
        copy.actionDescription = actionDescription;
        copy.commandName = commandName;
        return copy;
    }

    private Header(String placeholder) {
        super("");
    }

    private static String[] concat(String[] a, String[] b) {
        String[] result = new String[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
