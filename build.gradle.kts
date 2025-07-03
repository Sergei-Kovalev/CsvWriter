plugins {
    `java-library`
    `maven-publish`
    id("org.sonarqube") version "6.2.0.5505"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

val datafakerVersion = "2.4.3"
val jupiterVersion = "5.8.1"
val lombokVersion = "1.18.34"

dependencies {
    api("net.datafaker:datafaker:${datafakerVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter:${jupiterVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
}

group = "org.writer"
version = "1.0-SNAPSHOT"
description = "csv"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
