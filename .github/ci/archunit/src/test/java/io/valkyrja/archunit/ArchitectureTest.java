/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.archunit;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "io.valkyrja", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule interfaces_should_reside_in_contract_packages =
            classes()
                    .that().areInterfaces()
                    .and().areNotAnnotations()
                    .should().resideInAPackage("..contract..")
                    .because("All interfaces are contracts and should be in an appropriate namespace");

    @ArchTest
    public static final ArchRule contract_packages_should_only_contain_interfaces =
            classes()
                    .that().resideInAPackage("..contract..")
                    .should().beInterfaces()
                    .because("All classes in a contract namespace must be interfaces");

    @ArchTest
    public static final ArchRule interfaces_should_be_named_contract =
            classes()
                    .that().areInterfaces()
                    .and().areNotAnnotations()
                    .should().haveSimpleNameEndingWith("Contract")
                    .because("All interfaces are contracts and should be named appropriately");

    @ArchTest
    public static final ArchRule contract_named_classes_should_be_interfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Contract")
                    .should().beInterfaces()
                    .because("All classes with name Contract must be interfaces");

    @ArchTest
    public static final ArchRule throwables_should_reside_in_throwable_packages =
            classes()
                    .that().areAssignableTo(Throwable.class)
                    .should().resideInAPackage("..throwable..")
                    .because("All throwable classes should exist in an appropriate namespace");

    @ArchTest
    public static final ArchRule abstract_classes_should_reside_in_abstract_packages =
            classes()
                    .that().haveModifier(JavaModifier.ABSTRACT)
                    .and().areNotInterfaces()
                    .should().resideInAPackage("..abstract_..")
                    .because("All abstract classes should exist in an appropriate namespace");

    @ArchTest
    public static final ArchRule no_classes_should_have_abstract_in_name =
            noClasses()
                    .should().haveSimpleNameContaining("Abstract")
                    .because("All classes should not be named with abstract naming convention");

    @ArchTest
    public static final ArchRule no_classes_should_have_enum_in_name =
            noClasses()
                    .should().haveSimpleNameContaining("Enum")
                    .because("All classes should not be named with enum naming convention");
}
