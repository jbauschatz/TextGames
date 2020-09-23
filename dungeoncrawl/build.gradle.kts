import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.21"
}

group = "com.textgame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":engine"))
    implementation(kotlin("stdlib-jdk8"))

    // https://mvnrepository.com/artifact/com.diogonunes/JColor
    implementation("com.diogonunes:JColor:5.0.0")

    implementation("org.yaml:snakeyaml:1.21")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testImplementation("org.hamcrest:hamcrest:2.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

task<JavaExec>("playGame") {
    main = "com.textgame.dungeoncrawl.Main"
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in` // Forward standard input to the Java task
}

task<JavaExec>("aiPlay") {
    main = "com.textgame.dungeoncrawl.Main"
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in` // Forward standard input to the Java task

    args("--ai")
}