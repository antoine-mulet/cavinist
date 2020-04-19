import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val springBootVersion = "2.3.0.RC1"
    val kotlinVersion = "1.3.71"
    id("org.springframework.boot") version springBootVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion
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
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

val graphqlVersion = "2.1.0"
val kotestVersion = "4.0.4"
val mockkVersion = "1.9.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.expediagroup:graphql-kotlin-schema-generator:$graphqlVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphqlVersion")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

kapt {
    arguments {
        arg(
            "org.springframework.boot.configurationprocessor.additionalMetadataLocations",
            "$projectDir/src/main/resources")
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
}

tasks.register<Test>("it") {
    group = "verification"
    description = "Runs the integration tests only."
    filter {
        includeTestsMatching("*IT")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // We need the following compiler arg so null-safety taken into account in Kotlin types inferred from Spring API
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
