/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@AnalyzeClasses(packages = "io.valkyrja", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    /** The package a component's always-present categorical exceptions live in. */
    private static final String CATEGORICAL_PACKAGE = "throwable.exception.abstract_";

    /** The package that marks its parent as a component with something to register. */
    private static final String PROVIDER_PACKAGE = "provider";

    /** The categoricals every component ships, whether or not anything subclasses them yet. */
    private static final List<String> CATEGORICAL_SUFFIXES =
            List.of("RuntimeException", "InvalidArgumentException");

    @ArchTest
    public static final ArchRule io_grpc_is_confined_to_the_entry_package =
            noClasses()
                    .that()
                    .resideOutsideOfPackage("io.valkyrja.application.entry..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("io.grpc..")
                    .because(
                            "the native gRPC library is an optional (compileOnly) dependency; only"
                                    + " the entry package — the bridge and the per-runtime worker"
                                    + " adapters — may touch it, so core stays library-agnostic");

    @ArchTest
    public static final ArchRule interfaces_should_reside_in_contract_packages =
            classes()
                    .that()
                    .areInterfaces()
                    .and()
                    .areNotAnnotations()
                    .should()
                    .resideInAPackage("..contract..")
                    .because(
                            "All interfaces are contracts and should be in an appropriate namespace");

    @ArchTest
    public static final ArchRule contract_packages_should_only_contain_interfaces =
            classes()
                    .that()
                    .resideInAPackage("..contract..")
                    .should()
                    .beInterfaces()
                    .because("All classes in a contract namespace must be interfaces");

    @ArchTest
    public static final ArchRule interfaces_should_be_named_contract_or_throwable =
            classes()
                    .that()
                    .areInterfaces()
                    .and()
                    .areNotAnnotations()
                    .should()
                    .haveSimpleNameEndingWith("Contract")
                    .orShould()
                    .haveSimpleNameEndingWith("Throwable")
                    .because(
                            "All interfaces are contracts or throwable markers and should be named"
                                    + " appropriately");

    @ArchTest
    public static final ArchRule contract_named_classes_should_be_interfaces =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Contract")
                    .should()
                    .beInterfaces()
                    .because("All classes with name Contract must be interfaces");

    @ArchTest
    public static final ArchRule throwables_should_reside_in_throwable_packages =
            classes()
                    .that()
                    .areAssignableTo(Throwable.class)
                    .should()
                    .resideInAPackage("..throwable..")
                    .because("All throwable classes should exist in an appropriate namespace");

    @ArchTest
    public static final ArchRule abstract_classes_should_reside_in_abstract_or_factory_packages =
            classes()
                    .that()
                    .haveModifier(JavaModifier.ABSTRACT)
                    .and()
                    .areNotInterfaces()
                    .should()
                    .resideInAPackage("..abstract_..")
                    .orShould()
                    .resideInAPackage("..factory..")
                    .orShould()
                    .resideInAPackage("..controller..")
                    .orShould()
                    .resideInAPackage("..constant..")
                    .because(
                            "Abstract classes should exist in an abstract_, factory, controller, or"
                                    + " constant namespace");

    @ArchTest
    public static final ArchRule no_classes_should_have_abstract_in_name =
            noClasses()
                    .should()
                    .haveSimpleNameContaining("Abstract")
                    .because("All classes should not be named with abstract naming convention");

    @ArchTest
    public static final ArchRule no_classes_should_have_enum_in_name =
            noClasses()
                    .should()
                    .haveSimpleNameContaining("Enum")
                    .because("All classes should not be named with enum naming convention");

    /**
     * Every component ships both categorical exceptions, used or not, so a first-party or
     * third-party subclass can be added later without restructuring the hierarchy.
     *
     * <p>Expressed as a presence check rather than a fluent rule on purpose: the other rules here
     * constrain the classes that exist, and no predicate over existing classes can observe the one
     * that is missing.
     */
    @ArchTest
    public static void components_should_ship_both_categorical_exceptions(JavaClasses classes) {
        Map<String, Set<String>> categoricalsByPackage = new TreeMap<>();

        for (JavaClass clazz : classes) {
            parentOf(clazz.getPackageName(), CATEGORICAL_PACKAGE)
                    .ifPresent(
                            component ->
                                    categoricalsByPackage
                                            .computeIfAbsent(component, key -> new TreeSet<>())
                                            .add(clazz.getSimpleName()));
        }

        List<String> violations = new ArrayList<>();

        categoricalsByPackage.forEach(
                (component, names) ->
                        CATEGORICAL_SUFFIXES.stream()
                                .filter(
                                        suffix ->
                                                names.stream()
                                                        .noneMatch(name -> name.endsWith(suffix)))
                                .forEach(
                                        suffix ->
                                                violations.add(
                                                        component + " ships no *" + suffix)));

        assertTrue(
                violations.isEmpty(),
                "Every component always ships a *RuntimeException and a"
                        + " *InvalidArgumentException in its "
                        + CATEGORICAL_PACKAGE
                        + " package, even if currently unused:\n"
                        + String.join("\n", violations));
    }

    /**
     * A package holding a {@code provider} package is a component — it has something to register —
     * so it also owns a throwable hierarchy rooted at its categoricals.
     *
     * <p>The provider package is the machine-checkable stand-in for "is a component". It is
     * sufficient, not necessary: a component with nothing to register (http.client, http.struct)
     * still owns its categoricals, and this rule cannot see it.
     */
    @ArchTest
    public static void components_should_ship_a_throwable_package(JavaClasses classes) {
        Set<String> components = new TreeSet<>();
        Set<String> componentsWithCategoricals = new TreeSet<>();

        for (JavaClass clazz : classes) {
            String packageName = clazz.getPackageName();

            parentOf(packageName, PROVIDER_PACKAGE).ifPresent(components::add);
            parentOf(packageName, CATEGORICAL_PACKAGE).ifPresent(componentsWithCategoricals::add);
        }

        components.removeAll(componentsWithCategoricals);

        assertTrue(
                components.isEmpty(),
                "Every component owning a provider package also owns a "
                        + CATEGORICAL_PACKAGE
                        + " package; these do not:\n"
                        + String.join("\n", components));
    }

    /**
     * Resolve the package that owns the given sub-package.
     *
     * @param packageName the package to inspect
     * @param subPackage the dot-separated sub-package to look for
     * @return the owning package, or empty if this package is not that sub-package or below it
     */
    private static Optional<String> parentOf(String packageName, String subPackage) {
        String needle = "." + subPackage;
        int index = packageName.indexOf(needle);

        if (index < 0) {
            return Optional.empty();
        }

        int end = index + needle.length();

        // Guard a partial segment match — `.providers` must not read as `.provider`.
        if (end < packageName.length() && packageName.charAt(end) != '.') {
            return Optional.empty();
        }

        return Optional.of(packageName.substring(0, index));
    }
}
