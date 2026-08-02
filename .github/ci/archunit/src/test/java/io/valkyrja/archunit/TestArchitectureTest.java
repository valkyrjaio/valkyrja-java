/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Architecture rules for the JUnit build's test tree.
 *
 * <p>The test taxonomy is not the src taxonomy, so {@link ArchitectureTest}'s rules deliberately do
 * not apply here — pointing them at the tests produces only false positives. Tests legitimately use
 * {@code io.grpc} through the grpc-netty-shaded test transport, unit-test paths mirror src (so a
 * test can sit in a {@code contract} package without being an interface), and a test of the {@code
 * enum_} package is named for it. These rules are the Java spelling of the PHPArkitect test rules
 * the reference implementation enforces.
 *
 * <p>The classes are imported by path rather than scanned off the classpath so the two rule sets
 * stay strictly separated: src is checked by {@link ArchitectureTest}, the test tree only here.
 */
public class TestArchitectureTest {

    private static final JavaClasses TEST_TREE = importTestTree();

    private static JavaClasses importTestTree() {
        String property = System.getProperty("valkyrja.testTreeClasses");

        if (property == null || property.isBlank()) {
            throw new IllegalStateException(
                    "The valkyrja.testTreeClasses system property is not set; the test tree cannot"
                            + " be imported. It is configured by the `test` task in"
                            + " .github/ci/archunit/build.gradle.kts.");
        }

        List<Path> paths = Arrays.stream(property.split(File.pathSeparator)).map(Path::of).toList();

        return new ClassFileImporter().importPaths(paths);
    }

    @Test
    void test_classes_should_be_final() {
        ArchRule rule =
                classes()
                        .that()
                        .areTopLevelClasses()
                        .and()
                        .areNotInterfaces()
                        .and()
                        .areNotEnums()
                        .and()
                        .doNotHaveModifier(JavaModifier.ABSTRACT)
                        .should()
                        .haveModifier(JavaModifier.FINAL)
                        .because("All test classes should be final");

        rule.check(TEST_TREE);
    }

    @Test
    void fixtures_should_be_named_fixture() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..tests.fixtures..")
                        .and()
                        .areTopLevelClasses()
                        .and()
                        .areNotInterfaces()
                        .and()
                        .areNotEnums()
                        .should()
                        .haveSimpleNameEndingWith("Fixture")
                        .because("Testable fixtures should be named with a Fixture suffix");

        rule.check(TEST_TREE);
    }

    @Test
    void fixtures_should_not_be_named_test() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..tests.fixtures..")
                        .should()
                        .haveSimpleNameEndingWith("Test")
                        .because("Testable classes are not tests");

        rule.check(TEST_TREE);
    }

    @Test
    void only_tests_should_reside_in_unit_and_functional() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAnyPackage("..tests.unit..", "..tests.functional..")
                        .and()
                        .areTopLevelClasses()
                        .should()
                        .haveSimpleNameEndingWith("Test")
                        .because("Only tests should be in the unit and functional namespaces");

        rule.check(TEST_TREE);
    }
}
