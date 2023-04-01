import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val springBootVersion = "2.4.1"
    val kotlinVersion = "1.4.20"
    id("org.springframework.boot") version springBootVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    jacoco
}

// Using spring dependency management coming from spring boot - see https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/#managing-dependencies-using-in-isolation
apply(plugin = "io.spring.dependency-management")

the<DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

group = "com.amulet"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

val graphqlVersion = "3.6.8"
val bouncyCastleVersion = "1.67"
val passayVersion = "1.6.0"
val jjwtVersion = "0.11.2"

val kotestVersion = "4.3.1"
val springMockkVersion = "3.0.0"
val jacocoVersion = "0.8.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncyCastleVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.passay:passay:$passayVersion")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphqlVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
}

kapt {
    arguments {
        arg(
            "org.springframework.boot.configurationprocessor.additionalMetadataLocations",
            "$projectDir/src/main/resources")
    }
}

jacoco {
    toolVersion = jacocoVersion
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

tasks.withType<Test> {
    description = "Runs all the tests."
    useJUnitPlatform()
}

tasks.register<Test>("ut") {
    group = "verification"
    description = "Runs the unit tests only."
    systemProperty("parallelism", 2)
    filter {
        includeTestsMatching("*Tests")
    }
    configure<JacocoTaskExtension> {
        isEnabled = false
    }
}

tasks.register<Test>("it") {
    group = "verification"
    description = "Runs the integration tests only."
    filter {
        includeTestsMatching("*IT")
    }
    configure<JacocoTaskExtension> {
        isEnabled = false
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // We need the following compiler arg so null-safety taken into account in Kotlin types inferred from Spring API
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
