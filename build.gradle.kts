
plugins {
  id("java-library")
  id("otel.publish-conventions")
  id("otel.java-conventions")
}

apply(from = "version.gradle.kts")

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(project(":proto"))
}

description = "Java Bindings for the Opamp Protocol"
group = "io.opentelemetry"

// publishing {
//  publications.create<MavenPublication>("lib") {
//    from(components["java"])
//  }
//  repositories.maven("/tmp/opamp-java")
// }

tasks.jar {
  from(sourceSets.main.get().output)
  dependsOn(configurations.runtimeClasspath)
  from({
    configurations.runtimeClasspath.get().filter { it.name.equals("proto.jar") }.map { zipTree(it) }
  })
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
