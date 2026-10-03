plugins {
    application
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.lombok)
    alias(libs.plugins.spotless)
    alias(libs.plugins.versions)
    alias(libs.plugins.sentry)
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "hexlet.code.AppApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.actuator)
    developmentOnly(libs.spring.boot.devtools)

    implementation(libs.jackson.databind.nullable)
    implementation(libs.springdoc.openapi.ui)
    implementation(libs.sentry.spring.boot)

    runtimeOnly(libs.h2)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.datafaker)
    testImplementation(libs.instancio.junit)
    testImplementation(libs.json.unit.assertj)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    finalizedBy(tasks.jacocoTestReport)
}

spotless {
    java {
        target("src/**/*.java")
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

// Порог покрытия: ниже него `make build` падает, а вместе с ним и сборка в CI
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.check { dependsOn(tasks.jacocoTestCoverageVerification) }

// Исходники уходят в коллектор ошибок только при заданном токене (на CI и в продакшене)
sentry {
    autoInstallation.enabled = false
    includeSourceContext = System.getenv("SENTRY_AUTH_TOKEN") != null
    org = System.getenv("SENTRY_ORG")
    projectName = System.getenv("SENTRY_PROJECT")
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}
