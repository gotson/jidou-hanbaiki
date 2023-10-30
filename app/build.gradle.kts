plugins {
    id("java")
    application
}

group = "org.gotson.jidouhanbaiki"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.gotson.jidouhanbaiki.app.SampleApplication")
}

dependencies {
    implementation(project(":lib"))
    implementation("ch.qos.logback:logback-classic:1.4.11")
}