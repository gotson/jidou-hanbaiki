plugins {
    id("java-library")
}

group = "org.gotson.jidouhanbaiki"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("io.github.openfeign:feign-core:12.5")
    implementation("io.github.openfeign:feign-jackson:12.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("commons-io:commons-io:2.14.0")
    implementation("org.codehaus.plexus:plexus-archiver:4.8.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.wiremock:wiremock-standalone:3.1.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-core:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}